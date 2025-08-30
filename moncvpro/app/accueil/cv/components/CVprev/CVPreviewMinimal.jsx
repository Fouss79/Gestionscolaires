'use client';
import React from "react";

export default function CVPreviewMinimal({ couleur, data }) {
  return (
    <div
      id="cv-preview"
      style={{
        width: "716px",   // largeur exacte A4 en px (72dpi)
        height: "842px",  // hauteur A4
        backgroundColor: "#ffffff",
        boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
        border: "1px solid #e5e7eb",
        display: "flex",
        flexDirection: "column",
        boxSizing: "border-box",
        padding: "32px",   // marge intérieure
        overflow: "auto",
        fontFamily: "Arial, sans-serif",
        color: "#111827",
      }}
    >
      {/* Nom et titre */}
      <h1
        style={{
            color : couleur,
          fontSize: "32px",
          fontWeight: "bold",
          marginBottom: "4px",
        }}
      >
        {data?.prenom} {data?.nom}
      </h1>
      <p
        style={{
          fontSize: "16px",
          color: "#4B5563",
          marginBottom: "24px",
        }}
      >
        {data?.titre}
      </p>

      {/* Contact */}
      <div
        style={{
          marginBottom: "24px",
          fontSize: "14px",
          color: "#374151",
        }}
      >
        <p>
          {data?.contact?.email} | {data?.contact?.telephone} | {data?.contact?.adresse}
        </p>
      </div>

      {/* Compétences */}
      {data?.competences && (
        <div style={{ marginBottom: "24px" }}>
          <h2
            style={{
              fontSize: "18px",
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              paddingBottom: "4px",
              marginBottom: "12px",
              color: couleur,
            }}
          >
            Compétences
          </h2>
          <p style={{ color: "#374151", fontSize: "14px" }}>
            {data.competences.join(" • ")}
          </p>
        </div>
      )}

      {/* Expériences */}
      {data?.experiences && (
        <div style={{ marginBottom: "24px" }}>
          <h2
            style={{
              fontSize: "18px",
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              paddingBottom: "4px",
              marginBottom: "12px",
              color: couleur,
            }}
          >
            Expériences
          </h2>
          {data.experiences.map((exp, i) => (
            <div key={i} style={{ marginTop: "12px" }}>
              <p
                style={{
                  fontWeight: "600",
                  fontSize: "15px",
                  color: couleur,
                }}
              >
                {exp.poste}
              </p>
              <p
                style={{
                  fontSize: "13px",
                  fontStyle: "italic",
                  color: "#4B5563",
                  marginBottom: "4px",
                }}
              >
                {exp.entreprise} | {exp.dates}
              </p>
              <p style={{ fontSize: "14px", color: "#374151" }}>
                {exp.description}
              </p>
            </div>
          ))}
        </div>
      )}

      {/* Formations */}
      {data?.formations && (
        <div>
          <h2
            style={{
              fontSize: "18px",
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              paddingBottom: "4px",
              marginBottom: "12px",
              color: couleur,
            }}
          >
            Formations
          </h2>
          {data.formations.map((f, i) => (
            <div key={i} style={{ marginTop: "12px" }}>
              <p
                style={{
                  fontWeight: "600",
                  fontSize: "15px",
                  color: couleur,
                }}
              >
                {f.diplome}
              </p>
              <p
                style={{
                  fontSize: "13px",
                  fontStyle: "italic",
                  color: "#4B5563",
                }}
              >
                {f.ecole} | {f.annee}
              </p>
            </div>
          ))}
        </div>
      )}
       
       {data?.interets && (
            <>
              <h3
                className="text-xl font-semibold border-b mb-2 mt-4"
              style={{
                color: couleur,
                fontWeight: "600",
                borderBottom: `2px solid ${couleur}`,
                paddingBottom: "6px",
                marginBottom: "14px",
              }}
              >
                Centres d'Interet
              </h3>
              <ul className="list-disc list-inside text-sm">
                {data.interets.map((l, i) => (
                  <li key={i}>{l}</li>
                ))}
              </ul>
            </>
          )}


    </div>
  );
}
