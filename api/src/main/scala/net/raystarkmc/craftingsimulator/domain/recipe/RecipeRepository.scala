package net.raystarkmc.craftingsimulator.domain.recipe

trait RecipeRepository[F[_]]:
  def resolveById(recipeId: RecipeId): F[Option[Recipe]]
  def save(recipe: Recipe): F[Unit]
  def delete(recipe: Recipe): F[Unit]