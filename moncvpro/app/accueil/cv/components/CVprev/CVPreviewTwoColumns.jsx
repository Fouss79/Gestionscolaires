'use client';
import React from "react";

export default function CVPreviewTwoColumns({ couleur, data }) {
  return (
    <div
      style={{
        width: "716px",       // A4 largeur en px (72dpi)
        minHeight: "842px",   // A4 hauteur
        backgroundColor: "#ffffff",
        boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
        padding: "32px",
        display: "grid",
        gridTemplateColumns: "1fr 1fr",
        gap: "24px",
        fontFamily: "sans-serif",
        boxSizing: "border-box",
      }}
    >
      {/* Colonne gauche */}
      <div>
        <h1 style={{ fontSize: "28px", fontWeight: "700", margin: "0 0 8px 0", color: couleur }}>
          {data?.prenom} {data?.nom}
        </h1>
        <p style={{ color: "#4B5563", marginBottom: "24px" }}>{data?.titre}</p>

        <h3 style={{
          fontWeight: "600",
          borderBottom: `2px solid ${couleur}`,
          paddingBottom: "4px",
          marginBottom: "8px",
          fontSize: "16px"
        }}>
          Contact
        </h3>
        <p style={{ margin: "2px 0" }}>{data?.contact?.email}</p>
        <p style={{ margin: "2px 0" }}>{data?.contact?.telephone}</p>
        <p style={{ margin: "2px 0" }}>{data?.contact?.adresse}</p>

        {data?.competences && (
          <div style={{ marginTop: "24px" }}>
            <h3 style={{
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              paddingBottom: "4px",
              marginBottom: "8px",
              fontSize: "16px"
            }}>
              Compétences
            </h3>
            <ul style={{ listStyleType: "disc", paddingLeft: "16px", margin: 0, fontSize: "14px" }}>
              {data.competences.map((c, i) => (
                <li key={i} style={{ marginBottom: "4px" }}>{c}</li>
              ))}
            </ul>
          </div>
        )}

        {data?.langues && (
            <>
              <h3 style={{
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              paddingBottom: "4px",
              marginBottom: "8px",
              fontSize: "16px"
            }}>
                Langues
              </h3>
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
          <div style={{ marginBottom: "24px" }}>
            <h3 style={{
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              paddingBottom: "4px",
              marginBottom: "12px",
              fontSize: "16px"
            }}>
              Expériences
            </h3>
            {data.experiences.map((exp, i) => (
              <div key={i} style={{ marginBottom: "12px" }}>
                <p style={{ fontWeight: "600", margin: "0 0 2px 0" }}>{exp.poste}</p>
                <p style={{ fontSize: "13px", fontStyle: "italic", margin: "0 0 2px 0" }}>
                  {exp.entreprise} | {exp.duree}
                </p>
                <p style={{ fontSize: "14px", margin: 0 }}>{exp.description}</p>
              </div>
            ))}
          </div>
        )}

        {data?.formations && (
          <div>
            <h3 style={{
              fontWeight: "600",
              borderBottom: `2px solid ${couleur}`,
              paddingBottom: "4px",
              marginBottom: "12px",
              fontSize: "16px"
            }}>
              Formations
            </h3>
            {data.formations.map((f, i) => (
              <div key={i} style={{ marginBottom: "12px" }}>
                <p style={{ fontWeight: "600", margin: "0 0 2px 0" }}>{f.diplome}</p>
                <p style={{ fontSize: "13px", fontStyle: "italic", margin: 0 }}>
                  {f.ecole} | {f.annee}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
