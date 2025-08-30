'use client';
import React from "react";

export default function CVPreviewModern({ couleur, data }) {
  const safeGray = "#4B5563"; // gris neutre

  return (
    <div
      id="cv-preview"
      style={{
        width: "716px",       // largeur A4
        height: "842px",      // hauteur A4
        backgroundColor: "#ffffff",
        boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
        display: "flex",
        boxSizing: "border-box",
        fontFamily: "Arial, sans-serif",
      
      }}
    >
      {/* Colonne gauche */}
      <div
        style={{
          width: "35%",
          backgroundColor: couleur,
          padding: "20px",
          display: "flex",
          flexDirection: "column",
          color: "white",
          boxSizing: "border-box",
        }}
      >
        {data?.photo && (
          <img
            src={data.photo}
            alt="photo"
            style={{
              width: "120px",
              height: "120px",
              borderRadius: "50%",
              objectFit: "cover",
              margin: "0 auto 20px auto",
            }}
          />
        )}

        <div style={{ marginBottom: "20px" }}>
          <h3 className="text-xl font-semibold border-b mb-2 mt-4"
            style={{
              fontWeight: "600",
              borderBottom: "1px solid white",
              paddingBottom: "6px",
              marginBottom: "12px",
            }}
          >
            Contacts
          </h3>
          <p style={{ marginBottom: "8px" }}>{data?.contact?.email}</p>
          <p style={{ marginBottom: "8px" }}>{data?.contact?.telephone}</p>
          <p style={{ marginBottom: "8px" }}>{data?.contact?.adresse}</p>
        </div>

        {data?.competences && (
  <div style={{ marginBottom: "20px" }}>
    <h3 className="text-xl font-semibold border-b mb-2 mt-4"
      style={{
        fontWeight: "600",
        borderBottom: "1px solid white",
        paddingBottom: "6px",
        marginBottom: "12px",
      }}
    >
      Compétences
    </h3 >
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
          }}
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
        fontWeight: "600",
        borderBottom: "1px solid white",
        paddingBottom: "6px",
        marginBottom: "12px",
      }}>
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
      <div
        style={{
          width: "65%",
          padding: "24px",
          boxSizing: "border-box",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <div style={{ textAlign: "center", marginBottom: "28px" }}>
          <h2
            style={{
              fontSize: "24px",
              fontWeight: "bold",
              color: couleur,
              marginBottom: "6px",
            }}
          >
            {data?.prenom} {data?.nom}
          </h2>
          <p style={{ fontSize: "15px", color: safeGray, margin: 0 }}>
            {data?.titre}
          </p>
        </div>

        {data?.experiences && (
          <div style={{ marginBottom: "24px" }}>
            <h3 className="text-xl font-semibold border-b mb-2 mt-4"
              style={{
                color: couleur,
                fontWeight: "600",
                borderBottom: `2px solid ${couleur}`,
                paddingBottom: "6px",
                marginBottom: "14px",
              }}
            >
              Expériences
            </h3>
            {data.experiences.map((exp, i) => (
              <div key={i} style={{ marginBottom: "14px" }}>
                <p
                  style={{
                    fontWeight: "600",
                    marginBottom: "4px",
                    color: safeGray,
                  }}
                >
                  {exp.poste}
                </p>
                <p
                  style={{
                    fontStyle: "italic",
                    fontSize: "13px",
                    marginBottom: "6px",
                    color: safeGray,
                  }}
                >
                  {exp.entreprise} | {exp.dates}
                </p>
                <p
                  style={{ fontSize: "13px", margin: 0, color: safeGray }}
                >
                  {exp.description}
                </p>
              </div>
            ))}
          </div>
        )}

        {data?.formations && (
          <div>
            <h3 className="text-xl font-semibold border-b mb-2 mt-4"
              style={{
                color: couleur,
                fontWeight: "600",
                borderBottom: `2px solid ${couleur}`,
                paddingBottom: "6px",
                marginBottom: "14px",
              }}
            >
              Formations
            </h3>
            {data.formations.map((f, i) => (
              <div key={i} style={{ marginBottom: "14px" }}>
                <p
                  style={{
                    fontWeight: "600",
                    marginBottom: "4px",
                    color: safeGray,
                  }}
                >
                  {f.diplome}
                </p>
                <p
                  style={{
                    fontStyle: "italic",
                    fontSize: "13px",
                    margin: 0,
                    color: safeGray,
                  }}
                >
                  {f.ecole} | {f.annee}
                </p>
              </div>
            ))}

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
              <ul className=" list-disc list-inside text-sm">
                {data.interets.map((l, i) => (
                  <li key={i}>{l}</li>
                ))}
              </ul>
            </>
          )}

          </div>
        )}
      </div>
    </div>
  );
}
