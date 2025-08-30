// components/TemplateSelector.js
"use client";
import React from "react";
import CVPreviewModern from "./CVPreviewModern";
import CVPreviewElegant from "./CVPreviewElegant";
import CVPreviewA4CompactFull from "./CVPreviewA4CompactFull";

const TemplateSelector = ({ selected, onSelect, cvData }) => {
  const templates = [
    { id: 1, name: "Moderne", component: CVPreviewModern },
    { id: 2, name: "Élégant", component: CVPreviewElegant },
    { id: 3, name: "Compact A4", component: CVPreviewA4CompactFull },
  ];

  return (
    <div className="flex flex-col gap-4">
      <h3 className="font-semibold">Choisir un modèle :</h3>
      <div className="flex gap-4">
        {templates.map((tpl) => {
          const TemplateComponent = tpl.component;
          return (
            <div
              key={tpl.id}
              onClick={() => onSelect(tpl.id)}
              className={`border rounded cursor-pointer overflow-hidden shadow-md transform transition
                ${selected === tpl.id ? "border-blue-500 scale-105" : "border-gray-300 hover:scale-105"}`}
              style={{ width: "150px", height: "210px" }}
            >
              {/* Miniature avec les données de l'utilisateur */}
              <div className="w-full h-full scale-[0.35] origin-top-left">
                {cvData ? <TemplateComponent data={cvData} /> : <p className="text-gray-400 text-sm text-center mt-20">Pas de données</p>}
              </div>

              {/* Nom du template */}
              <div className="bg-gray-100 text-center text-sm py-1">
                {tpl.name}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default TemplateSelector;
