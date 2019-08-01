package pl.edu.agh.wmazur.avs.model.entity

trait Identifiable {
  type Id = Long
  def id: Id
}
