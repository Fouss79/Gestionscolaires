'use client';
import React from "react";

export default function CVPreviewA4CompactFull({ couleur, data }) {

const sectionTitle = {
  fontWeight: "600",
  fontSize: "13px",
  borderBottom: `2px solid ${couleur}`,
  margin: "8px 0 10px 0",  // espace avant et après
  paddingBottom: "4px",    // espace avec le trait
};


  return (
    <div
      id="cv-preview"
      style={{
        width: "716px",   // largeur A4
        height: "842px",  // hauteur A4
        backgroundColor: "#ffffff",
        boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
        padding: "16px",
        boxSizing: "border-box",
        display: "grid",
        gridTemplateColumns: "1fr 2fr",
        gap: "12px",
        fontSize: "12px",
        lineHeight: "1.4",
      }}
    >
      {/* Colonne gauche */}
      <div
        style={{
          borderRight: `2px solid ${couleur}`,
          paddingRight: "12px",
          display: "flex",
          flexDirection: "column",
          gap: "12px",
        }}
      >
        {data?.photo && (
          <img
            src={data.photo}
            alt="Photo"
            style={{
              width: "90px",
              height: "90px",
              borderRadius: "50%",
              objectFit: "cover",
              margin: "0 auto",
              border: "1px solid #ccc",
            }}
          />
        )}

        <div style={{ textAlign: "center" }}>
          <h1 style={{ fontWeight: "bold", fontSize: "14px", color: couleur, margin: "4px 0" }}>
            {data?.prenom} {data?.nom}
          </h1>
          <p style={{ fontSize: "11px", color: "#4B5563", margin: 0 }}>{data?.titre}</p>
        </div>

        <div>
          <h2
            style={{
              fontWeight: "600",
              fontSize: "12px",
              borderBottom: `2px solid ${couleur}`,
              marginBottom: "6px",
              paddingBottom: "2px",
            }}
          >
            Contact
          </h2>
          <ul style={{ margin: 0, paddingLeft: "16px", listStyle: "disc" }}>
            {data?.contact?.email && <li>{data.contact.email}</li>}
            {data?.contact?.telephone && <li>{data.contact.telephone}</li>}
            {data?.contact?.adresse && <li>{data.contact.adresse}</li>}
            {data?.contact?.linkedin && <li>{data.contact.linkedin}</li>}
          </ul>
        </div>

        {data?.competences && (
          <div>
            <h2
              style={
               sectionTitle
              }
            >
              Compétences
            </h2>
            <ul style={{ margin: 0, paddingLeft: "16px", listStyle: "disc" }}>
              {data.competences.map((c, i) => (
                <li key={i}>{c}</li>
              ))}
            </ul>
          </div>
        )}

        {data?.langues && (
          <div>
            <h2
              style={{
                fontWeight: "600",
                fontSize: "12px",
                borderBottom: `2px solid ${couleur}`,
                marginBottom: "6px",
                paddingBottom: "2px",
              }}
            >
              Langues
            </h2>
            <ul style={{ margin: 0, paddingLeft: "16px", listStyle: "disc" }}>
              {data.langues.map((l, i) => (
                <li key={i}>{l}</li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {/* Colonne droite */}
      <div style={{ display: "flex", flexDirection: "column", gap: "12px", paddingLeft: "12px" }}>
        {data?.experiences && (
          <div>
            <h2
              style={{
                fontWeight: "600",
                fontSize: "13px",
                borderBottom: `2px solid ${couleur}`,
                marginBottom: "8px",
                paddingBottom: "2px",
              }}
            >
              Expériences
            </h2>
            {data.experiences.map((exp, i) => (
              <div key={i} style={{ marginBottom: "6px" }}>
                <p style={{ fontWeight: "bold", fontSize: "12px", margin: 0, color: couleur }}>
                  {exp.poste}
                </p>
                <p style={{ fontSize: "11px", fontStyle: "italic", margin: 0 }}>
                  {exp.entreprise} | {exp.duree}
                </p>
                <p style={{ fontSize: "11px", margin: "2px 0 0 0" }}>{exp.description}</p>
              </div>
            ))}
          </div>
        )}

        {data?.formations && (
          <div>
            <h2
              style={{
                fontWeight: "600",
                fontSize: "13px",
                borderBottom: `2px solid ${couleur}`,
                marginBottom: "8px",
                paddingBottom: "2px",
              }}
            >
              Formations
            </h2>
            {data.formations.map((f, i) => (
              <div key={i} style={{ marginBottom: "6px" }}>
                <p style={{ fontWeight: "bold", fontSize: "12px", margin: 0, color: couleur }}>
                  {f.diplome}
                </p>
                <p style={{ fontSize: "11px", fontStyle: "italic", margin: 0 }}>
                  {f.ecole} | {f.annee}
                </p>
              </div>
            ))}
          </div>
        )}

        {data?.interets && (
            <>
              <h2
              style={{
                fontWeight: "600",
                fontSize: "13px",
                borderBottom: `2px solid ${couleur}`,
                marginBottom: "8px",
                paddingBottom: "2px",
              }}
            >

                Centres d'Interet
              </h2>
              <ul className="list-disc list-inside text-sm "style={{ fontWeight: "bold", fontSize: "12px", margin: 0, color: couleur }}>
                {data.interets.map((l, i) => (
                  <li key={i}>{l}</li>
                ))}
              </ul>
            </>
          )}

      </div>
    </div>
  );
}
