"use client";
import React from "react";
import CVPreviewMinimal from "./CVprev/CVPreviewMinimal";
import CVPreviewModern from "./CVprev/CVPreviewModern";
import CVPreviewElegant from "./CVprev/CVPreviewElegant";
import CVPreviewA4CompactFull from "./CVPreviewA4CompactFull";
import CVPreviewTwoColumns from "./CVprev/CVPreviewTwoColumns";
import CVPreviewCreative from "./CVprev/CVPreviewCreative";

const TemplateSelector = ({ selected, onSelect, cvData, couleurChoisie }) => {
  const templates = [
    { id: 1, name: "Moderne", component: CVPreviewModern },
    { id: 2, name: "Élégant", component: CVPreviewElegant },
    { id: 4, name: "Compact A4", component: CVPreviewA4CompactFull },
    { id: 5, name: "Two columns", component: CVPreviewTwoColumns },
    { id: 6, name: "Creative", component: CVPreviewCreative },
    { id: 3, name: "Minimal", component: CVPreviewMinimal },
  ];

  return (
    <div className="flex flex-col gap-2 w-full">
       <h3 className="font-semibold">Choisir un modèle :</h3>
      {/* Grille de templates */}
      <div className="grid grid-cols-3 gap-x-4 gap-y-6 mt-6">
        {templates.map((tpl) => {
          const TemplateComponent = tpl.component;
          return (
            <div
              key={tpl.id}
              onClick={() => onSelect(tpl.id)}
              className={`border rounded cursor-pointer overflow-hidden shadow-md transform transition
                ${
                  selected === tpl.id
                    ? "border-blue-500 scale-105"
                    : "border-gray-300 hover:scale-105"
                }`}
              style={{ width: "150px", height: "210px" }}
            >
              {/* Miniature avec données utilisateur */}
              <div className="w-[420px] h-[594px] scale-[0.35] origin-top-left">
                {cvData ? (
                  <TemplateComponent couleur={couleurChoisie} data={cvData} />
                ) : (
                  <p className="text-gray-400 text-sm text-center mt-20">
                    Pas de données
                  </p>
                )}
              </div>

              {/* Nom du template */}
              <div className="bg-gray-100 text-center text-sm py-1 font-medium">
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
