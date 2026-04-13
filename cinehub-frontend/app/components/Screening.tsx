"use client";

import { MovieResponse } from "@/app/types/interfaces";
import { formatDate, getApiUrl } from "@/app/utils/functions";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import Select from "react-select";
import useSWR from "swr";
import { fetcher } from "@/app/utils/fetcher";
import Link from "next/link";

interface Screening {
  screeningId: number;
  movie: MovieResponse;
  startDate: string;
  room: {
    roomId: number;
    name: string;
  };
  price: number;
}

// TODO: generic button?

export default function Screening({ id }: { id: number }) {
  const { data: screeningResponse } = useSWR<Screening[]>(
    `${getApiUrl()}/screenings?movieId=${id}`,
    fetcher,
  );
  const [selectedDate, setSelectedDate] = useState<string>("");
  const [selectedScreening, setSelectedScreening] = useState<Screening | null>(
    null,
  );



  const router = useRouter();

  const uniqueDates = screeningResponse
    ? Array.from(
        new Set(
          screeningResponse
            .filter((screening) => new Date(screening.startDate) > new Date())
            .map((screening) => screening.startDate.split("T")[0]),
        ),
      ).sort()
    : [];

  const dateOptions = uniqueDates.map((date) => ({
    value: date,
    label: formatDate(date),
  }));

  useEffect(() => {
    if (uniqueDates.length > 0 && !selectedDate) {
      setSelectedDate(uniqueDates[0]);
    }
  }, [uniqueDates, selectedDate]);

  const handleTimeClick = (screeningId: number) => {
    const screening =
      screeningResponse?.find((s) => s.screeningId === screeningId) || null;
    setSelectedScreening(screening);
  };

  const handleReservation = () => {
    if (selectedScreening) {
      const reservationData = {
        title: selectedScreening.movie.title,
        fullDate: selectedScreening.startDate,
        room: {
          roomId: selectedScreening.room.roomId,
          name: selectedScreening.room.name,
        },
        screeningId: selectedScreening.screeningId,
        price: selectedScreening.price,
      };
      sessionStorage.setItem("reservation", JSON.stringify(reservationData));
      router.push("/reservation");
    }
  };

  return (
    <div className="flex flex-col gap-4">
      {uniqueDates.length > 0 ? (
        <>
          <div className="flex items-center gap-x-4 mt-5">
            <p className="font-bold">Date:</p>
            <div className="w-64">
              <Select
                value={dateOptions.find(
                  (option) => option.value === selectedDate,
                )}
                onChange={(selectedOption) =>
                  setSelectedDate(selectedOption?.value || "")
                }
                options={dateOptions}
                placeholder="Select a date"
                styles={{
                  control: (base) => ({
                    ...base,
                    backgroundColor: "",
                    color: "white",
                    borderColor: "rgb(55, 65, 81)",
                    borderRadius: "0.375rem",
                    boxShadow: "none",
                    "&:hover": { borderColor: "rgb(249, 115, 22)" },
                  }),
                  option: (base, { isFocused, isSelected }) => ({
                    ...base,
                    backgroundColor: isFocused
                      ? "rgb(249, 115, 22)"
                      : isSelected
                        ? "rgb(249, 115, 22)"
                        : "rgb(31, 41, 55)",
                    color: "white",
                    cursor: "pointer",
                    "&:active": {
                      backgroundColor: "rgb(249, 115, 22)",
                    },
                  }),
                  menu: (base) => ({
                    ...base,
                    backgroundColor: "rgb(31, 41, 55)",
                    borderRadius: "0.375rem",
                    marginTop: "5px",
                    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
                  }),
                  menuList: (base) => ({
                    ...base,
                    maxHeight: "150px",
                    overflowY: "auto",
                    "::-webkit-scrollbar": {
                      width: "6px",
                      height: "6px",
                    },
                    "::-webkit-scrollbar-thumb": {
                      backgroundColor: "rgb(55, 65, 81)",
                      borderRadius: "8px",
                    },
                    "::-webkit-scrollbar-track": {
                      backgroundColor: "rgb(31, 41, 55)",
                    },
                  }),
                  singleValue: (base) => ({
                    ...base,
                    color: "white",
                  }),
                  placeholder: (base) => ({
                    ...base,
                    color: "rgb(156, 163, 175)",
                  }),
                }}
              />
            </div>
          </div>

          <div className="flex items-center gap-x-4 mt-2">
            <p className="font-bold">Time:</p>
            <div className="flex items-center gap-x-4">
              {screeningResponse
                ?.filter(
                  (screening) =>
                    screening.startDate.startsWith(selectedDate) &&
                    new Date(screening.startDate) > new Date(),
                )
                .map((screening) => (
                  <p
                    key={screening.screeningId}
                    onClick={() => handleTimeClick(screening.screeningId)}
                    className={`${
                      selectedScreening?.screeningId === screening.screeningId
                        ? "bg-orange-500"
                        : "bg-transparent"
                    } rounded-2xl px-2 py-1`}
                  >
                    {new Date(screening.startDate).toLocaleTimeString([], {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </p>
                ))}
            </div>
          </div>

          <div className="flex items-center justify-between">
            <button
              onClick={handleReservation}
              disabled={!selectedScreening}
              className={`flex w-fit my-3 justify-center items-center px-4 py-3 text-md ${
                selectedScreening
                  ? "bg-orange-500 hover:bg-orange-600"
                  : "bg-neutral-700"
              } rounded-3xl transition duration-200 ease-in-out`}
            >
              <p className="text-zinc-900 font-bold text-md lg:text-lg">
                Choose seat
              </p>
            </button>
            <div className="flex gap-x-4">
              <Link href={"/addReview?movieId=" + id}>
                <button className="flex w-fit my-3 justify-center items-center px-4 py-3 text-md bg-orange-500 hover:bg-orange-600 rounded-3xl transition duration-200 ease-in-out">
                  <p className="text-zinc-900 font-bold text-md lg:text-lg">
                    Add review
                  </p>
                </button>
              </Link>

              <Link href={"/soldTickets?movieId=" + id}>
                <button className="flex w-fit my-3 justify-center items-center px-4 py-3 text-md bg-orange-500 hover:bg-orange-600 rounded-3xl transition duration-200 ease-in-out">
                  <p className="text-zinc-900 font-bold text-md lg:text-lg">
                    Get statistics
                  </p>
                </button>
              </Link>
            </div>
          </div>
        </>
      ) : (
        <p className="text-lg text-neutral-500 mt-10">
          No screenings available.
        </p>
      )}
    </div>
  );
}
