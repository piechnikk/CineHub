"use client";
import Image from "next/image";
import { MovieResponse } from "@/app/types/interfaces";
import { convertToHours, getApiUrl } from "@/app/utils/functions";
import KeyboardDoubleArrowDownIcon from "@mui/icons-material/KeyboardDoubleArrowDown";
import useSWR from "swr";
import { fetcher } from "@/app/utils/fetcher";

export default function Hero(props: MovieResponse) {
  const handleScroll = () => {
    window.scrollTo({
      top: window.innerHeight,
      behavior: "smooth",
    });
  };

  const { data, error } = useSWR(
    `${getApiUrl()}/reviews/rating/` + props.movieId,
    fetcher,
  );

  return (
    <div className="relative w-full h-screen">
      <div className="absolute inset-0 bg-black opacity-60 z-10"></div>
      <Image
        src={props.bg_img}
        alt="Hero background"
        className="absolute inset-0 object-cover object-center"
        fill
        priority
      />

      <div className="absolute w-full lg:w-3/5 h-screen flex justify-center items-center z-20">
        <div className="mx-16">
          <div>
            <p className="text-white text-5xl lg:text-7xl 3xl:text-8xl font-bold font-oswald">
              {props.title}
            </p>
          </div>
          <div className="flex gap-4 my-4 font-oswald">
            <p className="text-white text-lg lg:text-2xl">
              Rating: {error ? "--" : data} / 5
            </p>
            <p className="text-white text-lg lg:text-2xl">
              Duration: {convertToHours(props.duration)}
            </p>
            <p className="text-white text-lg lg:text-2xl">
              Release year: {props.publishDate}
            </p>
          </div>
          <div className="my-8">
            <p className="text-white text-md lg:text-xl">{props.description}</p>
          </div>
          <div className="flex gap-6 mt-10">
            <button className="flex justify-center items-center px-4 py-3 bg-orange-500 rounded-3xl transition duration-200 ease-in-out hover:bg-orange-600">
              <span className="material-icons">play_arrow</span>
              <p className="text-zinc-900 font-bold text-md lg:text-lg">
                Watch Trailer
              </p>
            </button>
            <button className="flex justify-center items-center px-4 py-3 bg-neutral-100 rounded-3xl transition duration-200 ease-in-out hover:bg-neutral-300">
              <span className="material-icons">add</span>
              <p className="text-zinc-900 font-bold text-md lg:text-lg">
                Buy Ticket
              </p>
            </button>
          </div>
        </div>
      </div>

      <div className="absolute bottom-10 left-1/2 z-30 transform -translate-x-1/2">
        <div
          className="border-neutral-100 text-neutral-100 border-2 rounded-full flex justify-center items-center transition duration-150 ease-in-out hover:bg-neutral-300 hover:text-zinc-900 hover:cursor-pointer"
          onClick={handleScroll}
        >
          <span className="p-2 ">
            <KeyboardDoubleArrowDownIcon fontSize="large" />
          </span>
        </div>
      </div>
    </div>
  );
}
