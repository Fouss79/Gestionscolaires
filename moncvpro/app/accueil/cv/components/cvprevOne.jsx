"use client";
import React from "react";

export default function CVPreview() {
 
  return (
    <section className="max-w-4xl mx-auto border rounded shadow bg-white overflow-hidden">
      {/* Header: Photo + Nom + Titre */}
      <div className="flex bg-gray-100 p-6 gap-6 items-center">
    
          <img
            src='/im2.jpeg'
            alt="Photo"
            className="w-32 h-32 object-cover rounded-full border border-gray-300"
          />
        
        <div className="flex flex-col">
          <h2 className="text-2xl font-bold text-gray-800">TRAORE</h2>
          <h4 className="text-sm text-gray-600">Deveoloppeur Fullstac</h4>
          <p className="mt-2 text-gray-700"></p>
        </div>
      </div>

      <div className="flex flex-col md:flex-row">
        {/* Sidebar gauche */}
        <aside className="bg-gray-800 text-white p-4 md:w-1/3">
          <h3 className="text-lg font-semibold mb-2">Contact</h3>
          <ul className="space-y-1 text-sm">
            <li><strong>Téléphone:</strong>79707010</li>
            <li><strong>Email:</strong>foussenytraore692@gmail.com</li>
            <li><strong>Adresse:</strong> Bamako</li>
          </ul>

          <h3 className="text-lg font-semibold mt-6 mb-2">Langues</h3>
          <ul className="list-disc list-inside text-sm">
              <li key={i}>Francais</li>
              <li key={i}>Anglais</li>
              <li key={i}>Bambara</li>
          </ul>

          <h3 className="text-lg font-semibold mt-6 mb-2">Centres d’intérêt</h3>
          <p className="text-sm">Sport</p>
        </aside>

        {/* Contenu principal */}
        <main className="p-6 md:w-2/3 space-y-6 text-sm text-gray-800">
          <div>
            <h3 className="text-lg font-semibold mb-2">Compétences</h3>
            <ul className="list-disc list-inside grid grid-cols-2 gap-x-4">
              <li>React</li>
               <li>Spring boot</li>
                <li>Talwins css</li>
                 <li>Docker</li>
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-semibold mb-2">Formations</h3>
            <ul className="space-y-1">
             
                <li>
                  <strong>Licence</strong> – FST <span className="text-gray-500">2017</span>
                </li>
                 <li>
                  <strong>Licence</strong> – ECOSUP-Alterance <span className="text-gray-500">2023</span>
                </li>
        
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-semibold mb-2">Expériences</h3>
            <ul className="space-y-4">
              
                <li>
                  <p className="font-medium">Enseignant chez LPEMK</p>
                  <p className="text-xs text-gray-500">2018</p>
                  
                </li>
            
            </ul>
          </div>
        </main>
      </div>
    </section>
  );
}
