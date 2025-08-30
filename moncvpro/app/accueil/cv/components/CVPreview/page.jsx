"use client";
import React from "react";

export default function CVPreview({ data }) {
  if (!data || !data.nom) {
    return <p className="text-gray-500 italic text-center mt-8">Remplissez le formulaire pour voir l’aperçu ici.</p>;
  }

  return (
    <section className="max-w-4xl mx-auto border rounded shadow bg-white overflow-hidden">
      {/* Header: Photo + Nom + Titre */}
      <div className="flex bg-gray-100 p-6 gap-6 items-center">
        {data.photo && (
          <img
            src={data.photo}
            alt="Photo"
            className="w-32 h-32 object-cover rounded-full border border-gray-300"
          />
        )}
        <div className="flex flex-col">
          <h2 className="text-2xl font-bold text-gray-800">{data.nom}</h2>
          <h4 className="text-sm text-gray-600">{data.titre}</h4>
          <p className="mt-2 text-gray-700">{data.profil}</p>
        </div>
      </div>

      <div className="flex flex-col md:flex-row">
        {/* Sidebar gauche */}
        <aside className="bg-gray-800 text-white p-4 md:w-1/3">
          <h3 className="text-lg font-semibold mb-2">Contact</h3>
          <ul className="space-y-1 text-sm">
            <li><strong>Téléphone:</strong> {data.contact.telephone}</li>
            <li><strong>Email:</strong> {data.contact.email}</li>
            <li><strong>Adresse:</strong> {data.contact.adresse}</li>
          </ul>

          <h3 className="text-lg font-semibold mt-6 mb-2">Langues</h3>
          <ul className="list-disc list-inside text-sm">
            {data.langues.map((langue, i) => <li key={i}>{langue}</li>)}
          </ul>

          <h3 className="text-lg font-semibold mt-6 mb-2">Centres d’intérêt</h3>
          <p className="text-sm">{data.interets}</p>
        </aside>

        {/* Contenu principal */}
        <main className="p-6 md:w-2/3 space-y-6 text-sm text-gray-800">
          <div>
            <h3 className="text-lg font-semibold mb-2">Compétences</h3>
            <ul className="list-disc list-inside grid grid-cols-2 gap-x-4">
              {data.competences.map((comp, i) => <li key={i}>{comp}</li>)}
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-semibold mb-2">Formations</h3>
            <ul className="space-y-1">
              {data.formations.map((f, i) => (
                <li key={i}>
                  <strong>{f.diplome}</strong> – {f.ecole} <span className="text-gray-500">({f.annee})</span>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-semibold mb-2">Expériences</h3>
            <ul className="space-y-4">
              {data.experiences.map((exp, i) => (
                <li key={i}>
                  <p className="font-medium">{exp.poste} chez {exp.entreprise}</p>
                  <p className="text-xs text-gray-500">{exp.dates}</p>
                  <ul className="list-disc list-inside ml-4 text-sm">
                    {exp.responsabilites.map((r, j) => <li key={j}>{r}</li>)}
                  </ul>
                </li>
              ))}
            </ul>
          </div>
        </main>
      </div>
    </section>
  );
}
