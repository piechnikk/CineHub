"use client";

import Navbar from "@/app/components/Navbar";
import SeatGenerator from "@/app/components/SeatGenerator";
import { Room, Seat, SeatProps } from "@/app/types/interfaces";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useRequireAuth } from "../hooks/useRequireAuth";
import { getApiUrl } from "../utils/functions";

export default function Reservation() {
  const [reservation, setReservation] = useState<{
    title: string;
    fullDate: string;
    room: Room;
    screeningId: number;
    price: number;
  } | null>(null);
  const [seats, setSeats] = useState<SeatProps[] | null>(null);
  const [selectedSeats, setSelectedSeats] = useState<Seat[]>([]);
  const [takenSeats, setTaken] = useState<number[]>([]);
  const router = useRouter();

  useRequireAuth(router);

  useEffect(() => {
    const storedReservation = sessionStorage.getItem("reservation");
    if (storedReservation) {
      setReservation(JSON.parse(storedReservation));
    }
  }, []);

  useEffect(() => {
    async function fetchSeats() {
      if (reservation?.room.roomId) {
        const res = await fetch(
          `${getApiUrl()}/room/seats/${reservation.room.roomId}`,
        );
        const data = await res.json();

        const res1 = await fetch(
          `${getApiUrl()}/screenings/${reservation.screeningId}/takenSeats`,
        );
        const data1: Seat[] = await res1.json();

        setSeats(data);
        setTaken(data1.map((seat) => seat.seatId));
      }
    }
    if (reservation) {
      fetchSeats();
    }
  }, [reservation]);

  const pickSeatAction = (seat: Seat) => {
    if (takenSeats.includes(seat.seatId)) return;

    if (selectedSeats.some((s) => s.seatId === seat.seatId)) {
      setSelectedSeats(selectedSeats.filter((seat) => seat != seat));
    } else {
      setSelectedSeats([...selectedSeats, seat]);
    }
  };

  const handleSeatsReservation = async () => {
    if (reservation == null) return;
    const tickets: { seatId: number; screeningId: number }[] = [];
    selectedSeats.forEach((seat) => {
      tickets.push({
        seatId: seat.seatId,
        screeningId: reservation.screeningId,
      });
    });

    const res = await fetch(`${getApiUrl()}/tickets`, {
      method: "POST",
      body: JSON.stringify(tickets),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (res.ok) {
      const data = await res.json();
      sessionStorage.setItem(
        "reservation",
        JSON.stringify({ ...reservation, tickets: data }),
      );
      router.push("/reservationConfirmation");
    } else if (res.status === 401) {
      toast.error("You are not logged in!");
      router.push("/auth/login");
    } else {
      const errorMessage = await res.text();
      toast.error(`Reservation failed: \n ${errorMessage}`);
    }
  };

  if (!reservation || !seats) {
    return (
      <div className="bg-zinc-900 max-w-screen min-h-screen text-neutral-100 flex items-center justify-center">
        <p>Loading seats...</p>
      </div>
    );
  }

  const [date, time] = reservation.fullDate.split("T");

  return (
    <div className="bg-zinc-900 max-w-screen min-h-screen text-neutral-100">
      <Navbar />
      <div className="container max-w-6xl mx-auto flex justify-center items-center flex-col">
        <div className="mt-10 flex flex-col items-center gap-y-4">
          <p className="text-5xl font-bold">{reservation.title}</p>
          <p className="text-2xl">
            {date} {time}
          </p>
          <p className="text-2xl">Room {reservation.room.name}</p>
        </div>

        <div className="flex justify-end items-center mt-10">
          <div className="mt-1 w-56 h-0.5 bg-neutral-400"></div>
          <span className="text-neutral-400 text-2xl mx-3">Screen</span>
          <div className="mt-1 w-56 h-0.5 bg-neutral-400"></div>
        </div>

        <SeatGenerator
          seats={seats}
          pickSeatAction={pickSeatAction}
          selectedSeats={selectedSeats}
          takenSeats={takenSeats}
        />

        <button
          onClick={handleSeatsReservation}
          disabled={selectedSeats.length === 0}
          className={`flex w-fit mb-10 justify-center items-center px-4 py-3 text-md ${
            selectedSeats.length !== 0
              ? "bg-orange-500 hover:bg-orange-600"
              : "bg-neutral-700"
          } rounded-3xl transition duration-200 ease-in-out`}
        >
          <p className="text-zinc-900 font-bold text-md lg:text-lg">Reserve</p>
        </button>
      </div>
    </div>
  );
}
