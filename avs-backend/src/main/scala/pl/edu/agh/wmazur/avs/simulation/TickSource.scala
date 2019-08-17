package pl.edu.agh.wmazur.avs.simulation

import java.util.concurrent.TimeUnit

import akka.actor.Cancellable
import akka.stream.scaladsl.Source

import scala.concurrent.duration._
object TickSource {
  type TickDelta = FiniteDuration
  val maxFrames = 60
  val timeScale = 1.0f
  val tickInterval: TickDelta = 1.second / maxFrames
  val timeStep: FiniteDuration =
    (tickInterval * timeScale).asInstanceOf[FiniteDuration]
  val timeStepSeconds: Double = timeStep.toUnit(TimeUnit.SECONDS)

  println(s"TickInterval: $tickInterval")

  val source: Source[TickDelta, Cancellable] = Source
    .tick(initialDelay = Duration.Zero,
          interval = tickInterval,
          tick = timeStep)
}
