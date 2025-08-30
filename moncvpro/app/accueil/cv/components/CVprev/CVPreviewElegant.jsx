'use client';
import React from "react";

export default function CVPreviewElegant({ couleur, data }) {
  return (
    <div
      id="cv-preview"
      style={{
        width: "716px",   // largeur A4
        height: "842px",  // hauteur A4
        backgroundColor: "#ffffff",
        boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
        border: "1px solid #e5e7eb",
        display: "flex",
        boxSizing: "border-box",
        flexDirection: "column",
        overflow: "hidden",
        
      }}
    >
      {/* Header */}
      <header
        className="text-white p-6 flex items-center mb-4"
        style={{ backgroundColor: couleur }}
      >
        {data?.photo && (
          <img
            src={data.photo}
            alt="photo"
            className="w-28 h-28 rounded-full border-4 border-white mr-6 object-cover"
          />
        )}
        <div>
          <h1 className="text-3xl font-bold">
            {data?.prenom} {data?.nom}
          </h1>
          <p
            className="text-lg"
            style={{ color: "#e0e7ff" }} // une nuance claire pour contraster
          >
            {data?.titre}
          </p>
          <p className="text-sm">
            {data?.contact?.email} | {data?.contact?.telephone}
          </p>
        </div>
      </header>

      {/* Corps */}
      <div className="flex-1 grid grid-cols-2 gap-6">
        {/* Colonne gauche */}
        <div>
          {data?.competences && (
  <div style={{ marginBottom: "20px" }}>
    <h3
      style={{
        fontWeight: "600",
        borderBottom: "1px solid white",
        paddingBottom: "6px",
        marginBottom: "12px",
        color: couleur
      }}
    >
      Compétences
    </h3>
    <ul
      style={{
        paddingLeft: "20px",
        fontSize: "13px",
        listStyleType: "disc",
      }}
    >
      {data.competences.map((c, i) => (
        <li
          key={i}
          style={{
            marginBottom: "12px", // 👈 augmenté pour plus d’espace
            lineHeight: "1.6",    // 👈 lisibilité améliorée
            
          }
        
        }
        >
          {c}
        </li>
      ))}
    </ul>
  </div>
)}


          {data?.langues && (
            <>
              <h2
                className="text-xl font-semibold border-b mb-2 mt-4"
                style={{ 
                  color: couleur }}
              >
                Langues
              </h2>
              <ul className="list-disc list-inside text-sm">
                {data.langues.map((l, i) => (
                  <li key={i}>{l}</li>
                ))}
              </ul>
            </>
          )}
        </div>

        {/* Colonne droite */}
        <div>
          {data?.experiences && (
            <>
              <h2
                className="text-xl font-semibold border-b mb-2"
                style={{ color: couleur }}
              >
                Expériences
              </h2>
              {data.experiences.map((exp, i) => (
                <div key={i} className="mb-2">
                  <p className="font-semibold">{exp.poste}</p>
                  <p className="text-sm italic">
                    {exp.entreprise} | {exp.duree}
                  </p>
                  <p className="text-sm">{exp.description}</p>
                </div>
              ))}
            </>
          )}

          {data?.formations && (
            <>
              <h2
                className="text-xl font-semibold border-b mb-2 mt-4"
                style={{ color: couleur }}
              >
                Formations
              </h2>
              {data.formations.map((f, i) => (
                <div key={i} className="mb-2">
                  <p className="font-semibold">{f.diplome}</p>
                  <p className="text-sm italic">
                    {f.ecole} | {f.annee}
                  </p>
                </div>
              ))}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
