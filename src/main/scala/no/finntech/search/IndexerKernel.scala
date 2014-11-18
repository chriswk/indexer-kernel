package no.finntech.search

import akka.actor.{ Actor, ActorSystem, Props }
import akka.kernel.Bootable

import nl.grons.metrics.scala._
import com.codahale.metrics._
import java.util.concurrent.TimeUnit

case object Start

class HelloActor extends ReceiveTimerActor with Instrumented {
    val indexActor = context.actorOf(Props[IndexActor])

    def receive = {
        case Start => indexActor ! "Hello"
        case message: String => {
            println("Received message '%s'" format message)
        }
    }

}

class IndexActor extends ReceiveTimerActor with Instrumented {
    def receive = {
        case message: String => sender() ! (message.toUpperCase + " world!")
    }

}

class MetricsReporter extends ReceiveTimerActor with Instrumented {
    def receive = {
        case Start => {
            val reporter = ConsoleReporter.forRegistry(IndexerKernel.metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build()
            reporter.start(1, TimeUnit.SECONDS)
        }
    }
}

object IndexerKernel {
    val metricRegistry = new com.codahale.metrics.MetricRegistry()
}

trait Instrumented extends InstrumentedBuilder {
    val metricRegistry = IndexerKernel.metricRegistry
}
class IndexerKernel extends Bootable {
    val system = ActorSystem("indexerKernel")

    def startup = {
        val hello = system.actorOf(Props[HelloActor])
        hello ! Start
        system.actorOf(Props[MetricsReporter]) ! Start
        (1 to 10).foreach { i: Int =>
            hello ! Start
            Thread sleep i * 500
        }
    }

    def shutdown = {
        system.shutdown()
    }
}
