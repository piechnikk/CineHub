import Hero from "@/app/components/Hero";
import Navbar from "@/app/components/Navbar";
import TrendingFilms from "@/app/components/TrendingFilms";
import { MovieResponse } from "@/app/types/interfaces";
import { getTrendingFilms } from "@/app/utils/functions";

export const dynamic = "force-dynamic";

export default async function Home() {
  const trending: MovieResponse[] = await getTrendingFilms();

  return (
    <div className="relative w-full select-none">
      <div className="w-full absolute top-0 z-20">
        <Navbar />
      </div>

      <Hero {...trending[0]} />

      <TrendingFilms />
    </div>
  );
}
