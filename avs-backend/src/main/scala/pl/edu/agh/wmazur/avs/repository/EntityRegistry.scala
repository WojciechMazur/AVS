//package pl.edu.agh.wmazur.avs.repository
//
//import akka.actor.typed.{ActorRef, Behavior}
//import akka.actor.typed.scaladsl.AbstractBehavior
//import pl.edu.agh.wmazur.avs.model.entity.Entity
//import pl.edu.agh.wmazur.avs.model.entity.intersection.Intersection
//import pl.edu.agh.wmazur.avs.protocol.Command
//
//class EntityRegistry extends AbstractBehavior[EntityRegistry.Protocol] {
//  override def onMessage(
//      msg: EntityRegistry.Protocol): Behavior[EntityRegistry.Protocol] =
//    Behavior.empty
//}
//
//object EntityRegistry {
//  sealed trait Protocol
//  object Protocol {
//    case class Register(entity: Entity, replyTo: ActorRef[Any])
//        extends Command[Protocol, Any]
//        with Protocol
//
//    case class Update(entity: Entity, replyTo: ActorRef[Any]) extends Command[Protocol, Any] with Protocol
//    case class
//  }
//}
