package net.raystarkmc.craftingsimulator.lib.transaction

import cats.*
import cats.data.*

trait Transaction[F[_], G[_]]:
  def withTransaction[A](program: F[A]): G[A]
  def withTransaction[E, A](program: EitherT[F, E, A]): EitherT[G, E, A]

object Transaction:
  def noop[F[_]]: Transaction[F, F] = new Transaction[F, F]:
    def withTransaction[A](program: F[A]): F[A] = program
    def withTransaction[E, A](program: EitherT[F, E, A]): EitherT[F, E, A] = program
