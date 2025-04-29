package net.raystarkmc.craftingsimulator.lib.transaction

import cats.~>

trait Transaction[F[_], G[_]]:
  self =>
  
  def withTransaction[A](program: F[A]): G[A]

  def mapK[H[_]](f: G ~> H): Transaction[F, H] = new Transaction[F, H] {
    override def withTransaction[A](program: F[A]): H[A] = f(self.withTransaction(program))
  }