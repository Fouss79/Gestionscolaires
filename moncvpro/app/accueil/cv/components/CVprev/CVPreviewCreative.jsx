'use client';
import React from "react";

export default function CVPreviewCreative({ couleur, data }) {
  // Dégradé du header
  const gradientStyle = (couleur) => ({
    background: `linear-gradient(90deg, ${couleur}, #9333ea)`, // violet
  });

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
      <div style={{ ...gradientStyle(couleur), color: "white", padding: "24px" }}>
        <h1 style={{ fontSize: "28px", fontWeight: "bold", margin: 0 }}>
          {data?.prenom} {data?.nom}
        </h1>
        <p style={{ fontSize: "16px", margin: 0 }}>{data?.titre}</p>
      </div>

      {/* Corps */}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr", gap: "16px", padding: "24px", flex: 1 }}>
        {/* Colonne gauche */}
        <div style={{ fontSize: "13px" }}>
          <h3
            style={{
              color: couleur,
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              marginBottom: "8px",
              paddingBottom: "4px", // 👈 espace ajouté
            }}
          >
            Contact
          </h3>
          <p>{data?.contact?.email}</p>
          <p>{data?.contact?.telephone}</p>
          <p>{data?.contact?.adresse}</p>

          {data?.competences && (
            <div style={{ marginTop: "16px" }}>
              <h3
                style={{
                  color: couleur,
                  fontWeight: "600",
                  borderBottom: `2px solid ${couleur}`,
                  marginBottom: "8px",
                  paddingBottom: "4px",
                }}
              >
                Compétences
              </h3>
              <ul style={{ paddingLeft: "18px", listStyleType: "disc", fontSize: "12px" }}>
                {data.competences.map((c, i) => (
                  <li key={i}>{c}</li>
                ))}
              </ul>
            </div>
          )}
        </div>

        {/* Colonne droite */}
        <div style={{ fontSize: "13px", display: "flex", flexDirection: "column", gap: "16px" }}>
          {data?.experiences && (
            <div>
              <h3
                style={{
                  color: couleur,
                  fontSize: "16px",
                  fontWeight: "600",
                  borderBottom: `2px solid ${couleur}`,
                  marginBottom: "8px",
                  paddingBottom: "4px",
                }}
              >
                Expériences
              </h3>
              {data.experiences.map((exp, i) => (
                <div key={i} style={{ marginBottom: "10px" }}>
                  <p style={{ fontWeight: "600", margin: 0 }}>{exp.poste}</p>
                  <p style={{ fontStyle: "italic", fontSize: "12px", margin: "2px 0" }}>
                    {exp.entreprise} | {exp.duree}
                  </p>
                  <p style={{ margin: "2px 0" }}>{exp.description}</p>
                </div>
              ))}
            </div>
          )}

          {data?.formations && (
            <div>
              <h3
                style={{
                  color: couleur,
                  fontSize: "16px",
                  fontWeight: "600",
                  borderBottom: `2px solid ${couleur}`,
                  marginBottom: "8px",
                  paddingBottom: "4px",
                }}
              >
                Formations
              </h3>
              {data.formations.map((f, i) => (
                <div key={i} style={{ marginBottom: "10px" }}>
                  <p style={{ fontWeight: "600", margin: 0 }}>{f.diplome}</p>
                  <p style={{ fontStyle: "italic", fontSize: "12px", margin: "2px 0" }}>
                    {f.ecole} | {f.annee}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
