
// CouleurSelector.js
'use client'
import React from "react";

const couleurs = [
  { id: "bleu", nom: "Bleu", hex: "#3b82f6" },
  { id: "rouge", nom: "Rouge", hex: "#ef4444" },
  { id: "vert", nom: "Vert", hex: "#22c55e" },
  { id: "orange", nom: "Orange", hex: "#f97316" },
];

export default function CouleurSelector({ selectedColor, onSelect }) {
  return (
    <div className="flex gap-4 mt-4">
      {couleurs.map((c) => (
        <div
          key={c.id}
          onClick={() => onSelect(c.hex)}
          className={`w-10 h-10 rounded-full cursor-pointer border-2 ${
            selectedColor === c.hex ? "border-black" : "border-transparent"
          }`}
          style={{ backgroundColor: c.hex }}
        />
      ))}
    </div>
  );
}
