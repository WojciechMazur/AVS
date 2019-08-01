package pl.edu.agh.wmazur.avs.model.entity.utils

import java.util.concurrent.atomic.AtomicLong

trait IdProvider[T] {
  private val counter = new AtomicLong(0)
  def nextId: Long = counter.incrementAndGet()
}
