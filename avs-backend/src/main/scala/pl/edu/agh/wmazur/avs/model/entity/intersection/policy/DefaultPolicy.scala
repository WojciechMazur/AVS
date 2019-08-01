package pl.edu.agh.wmazur.avs.model.entity.intersection.policy

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pl.edu.agh.wmazur.avs.model.entity.intersection.Vehicle2IntersectionManager
import pl.edu.agh.wmazur.avs.protocol.DriverProtocol

trait DefaultPolicy { self: Vehicle2IntersectionManager =>

  val defaultPolicy: Behavior[DriverProtocol] = Behaviors.empty
}
