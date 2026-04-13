import MovieInfo from "@/app/components/MovieInfo";
import Navbar from "@/app/components/Navbar";

export const dynamic = "force-dynamic";

export default async function MoviePage({
  params,
}: {
  params: Promise<{ id: number }>;
}) {
  const { id } = await params;

  return (
    <div className="bg-zinc-900 max-w-screen min-h-screen">
      <Navbar />
      <MovieInfo id={id} />
    </div>
  );
}
