package net.raystarkmc.craftingsimulator.lib.transaction

import cats.*
import cats.data.*

trait Transaction[F[_], G[_]]:
  self =>

  def withTransaction[A](program: F[A]): G[A]
  def withTransaction[E, A](program: EitherT[F, E, A]): EitherT[G, E, A]

  def mapK[H[_]](f: G ~> H): Transaction[F, H] = new Transaction[F, H]:
    def withTransaction[A](program: F[A]): H[A] =
      f(self.withTransaction(program))

    def withTransaction[E, A](program: EitherT[F, E, A]): EitherT[H, E, A] =
      self.withTransaction(program).mapK(f)

object Transaction:
  def noop[F[_]]: Transaction[F, F] = new Transaction[F, F]:
    def withTransaction[A](program: F[A]): F[A] = program
    def withTransaction[E, A](program: EitherT[F, E, A]): EitherT[F, E, A] = program
