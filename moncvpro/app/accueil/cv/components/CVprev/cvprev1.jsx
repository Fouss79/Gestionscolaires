'use client';
import React from "react";

export default function CVPreviewA4CompactFull({ data }) {
  return (
    <div className="w-[210mm] h-[297mm] bg-white shadow p-4 grid grid-cols-3 gap-2 text-[12px] leading-tight">
      {/* Colonne gauche */}
      <div className="col-span-1 flex flex-col gap-2 border-r pr-2">
        {data?.photo && (
          <img
            src={data.photo}
            alt="Photo"
            className="w-24 h-24 rounded-full mx-auto border"
          />
        )}
        <div className="text-center">
          <h1 className="font-bold text-[14px] uppercase">{data?.prenom} {data?.nom}</h1>
          <p className="text-[11px] text-gray-600">{data?.titre}</p>
        </div>

        <div>
          <h2 className="font-semibold text-[12px] border-b mb-1">Contact</h2>
          <ul className="space-y-0.5">
            {data?.contact?.email && <li>{data.contact.email}</li>}
            {data?.contact?.telephone && <li>{data.contact.telephone}</li>}
            {data?.contact?.adresse && <li>{data.contact.adresse}</li>}
            {data?.contact?.linkedin && <li>{data.contact.linkedin}</li>}
          </ul>
        </div>

        {data?.competences && (
          <div>
            <h2 className="font-semibold text-[12px] border-b mb-1">Compétences</h2>
            <ul className="space-y-0.5 list-disc list-inside">
              {data.competences.map((c, i) => <li key={i}>{c}</li>)}
            </ul>
          </div>
        )}

        {data?.langues && (
          <div>
            <h2 className="font-semibold text-[12px] border-b mb-1">Langues</h2>
            <ul className="space-y-0.5 list-disc list-inside">
              {data.langues.map((l, i) => <li key={i}>{l}</li>)}
            </ul>
          </div>
        )}
      </div>

      {/* Colonne droite */}
      <div className="col-span-2 flex flex-col gap-2 pl-2">
        {data?.experiences && (
          <div>
            <h2 className="font-semibold text-[12px] border-b mb-1">Expériences</h2>
            {data.experiences.map((exp, i) => (
              <div key={i} className="mb-1">
                <p className="font-bold text-[12px]">{exp.poste}</p>
                <p className="text-[11px] italic">{exp.entreprise} | {exp.duree}</p>
                <p className="text-[11px]">{exp.description}</p>
              </div>
            ))}
          </div>
        )}

        {data?.formations && (
          <div>
            <h2 className="font-semibold text-[12px] border-b mb-1 mt-2">Formations</h2>
            {data.formations.map((f, i) => (
              <div key={i} className="mb-1">
                <p className="font-bold text-[12px]">{f.diplome}</p>
                <p className="text-[11px] italic">{f.ecole} | {f.annee}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
