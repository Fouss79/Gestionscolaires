'use client';
import { useState,useEffect } from "react";
import React from "react";
import { FileText, Pencil, Eye, ThumbsUp } from "lucide-react";
import Particles from "react-tsparticles";
import SliderBackground from "../component/Sliderbackground";
import Link from "next/link";

export default function HomePage() {

  return (
    <div className="bg-white text-gray-800 font-sans">
      {/* Header */}
      <header className="flex justify-between items-center p-6 shadow-md">
        <h1 className="text-2xl font-bold text-gray-800 italic">
          Mon<span className="text-yellow-700">CVPro</span>
        </h1>
        <button className=" text-gray-700 font-bold">
          Se connecter
        </button>
      </header>

      {/* Hero */}
      <main>
        <section className="flex flex-col md:flex-row">
          <div className="w-full md:w-1/2 h-auto md:h-[400px] text-center p-10 bg-gray-700 text-white rounded-b-[80px] md:rounded-none md:rounded-br-[80px]">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Créez un CV parfait en quelques minutes
            </h2>
            <p className="text-base md:text-lg mb-6">
              Des modèles professionnels et des conseils personnalisés pour vous
              aider à décrocher l'emploi de vos rêves.
            </p>
            <button className="bg-[#14717D] text-white px-6 py-3 rounded text-lg hover:bg-blue-700">
              Commencer maintenant
            </button>
          </div>
          <div
            className="w-full md:w-1/2 h-[250px] md:h-[400px] bg-cover bg-center"
            style={{ backgroundImage: "url('/WhatsApp Image 2025-08-01 at 14.30.48.jpeg')" }}
          ></div>
        </section>

        {/* Étapes */}
        <section className="bg-gray-700 w-full">
  <div className="bg-white rounded-tl-[80px] p-6">
    <h3 className="text-3xl md:text-5xl font-bold text-gray-600 text-center mt-4 mb-12 italic">
      Comment ça fonctionne ?
    </h3>

    <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
      {[
        {
          img: "/im2.jpeg",
          title: "1. Choisissez un modèle",
          desc: "Sélectionnez parmi des modèles de CV professionnels conçus par des experts.",
        },
        {
          img: "/im3.jpeg",
          title: "2. Renseignez vos infos",
          desc: "Complétez votre CV avec vos expériences, compétences et formation.",
        },
        {
          img: "/im1.jpeg",
          title: "3. Prévisualisez et téléchargez",
          desc: "Vérifiez l’apparence finale et téléchargez votre CV en PDF.",
        },
      ].map((step, idx) => (
        <div key={idx} className="text-black px-4">
          <div
            className="bg-cover bg-center w-full h-[250px] rounded-xl mb-4"
            style={{ backgroundImage: `url('${step.img}')` }}
          ></div>

          {/* Badge numéro stylisé */}
          <span className="inline-block bg-gray-800 text-white font-bold text-lg px-4 py-1 mb-2 rounded-tr-[40px] rounded-bl-[40px]">
            {idx + 1}.
          </span>

          <h4 className="text-xl font-semibold mb-2">
            {step.title.split(". ")[1]}
          </h4>

          <p className="text-sm">{step.desc}</p>
        </div>
      ))}
    </div>
   <div className="flex justify-center mt-12">
  <Link href="/accueil/cv" passHref>
    <button className="bg-[#14717D] font-bold text-white px-6 py-3 rounded-2xl text-lg hover:bg-blue-700">
      Créer mon CV
    </button>
  </Link>
</div>

  </div>
</section>

        {/* Section de présentation */}
        <section
          className="bg-[#FAE7C3]  mb-8 p-6 md:p-20 mx-auto flex flex-col md:flex-row items-center gap-6"
          style={{ borderRadius: "0px 80px 0px 80px" }}
        >
          <div className="w-full md:w-1/4">
            <div
              className="bg-cover w-full h-[180px] rounded-xl"
              style={{ backgroundImage: "url('/imcv.jpeg')" }}
            ></div>
          </div>
          <div className="w-full md:w-3/4 px-2 flex flex-col text-center md:text-left">
            <h1 className="text-2xl md:text-3xl font-bold">
              Perfectionnez votre CV avec MonCVPro
            </h1>
            <p className="mt-4">
              Utilisez notre outil d'optimisation qui vérifie pour vous les 30
              erreurs potentielles les plus communes sur un CV, et laissez-vous
              guider par les conseils et suggestions de contenus professionnels.
            </p>
            <div className="flex justify-center md:justify-start mt-8">
              <button className="bg-[#14717D] hover:bg-blue-700 font-bold text-white px-6 py-3 rounded-2xl text-lg ">
                Perfectionner mon CV
              </button>
            </div>
          </div>
        </section>

        {/* Mise en page */}
        <section className="w-full bg-gray-500">
          <div
            className="bg-[#FAE7C3] flex flex-col items-center px-6 md:px-20 py-10 rounded-bl-[100px]"
          >
            <h1 className="text-2xl md:text-4xl font-bold text-gray-800 text-center mb-10">
              Créez facilement un CV parfait grâce à nos outils de mise en page performants
            </h1>
            <div className="flex flex-col md:flex-row w-full gap-6">
              <div className="w-full md:w-1/2 space-y-4">
                {[
                  "1. Modèle conçu pour les professionnels",
                  "2. Contenus pré-rédigés par des experts",
                  "3. Outil CV check",
                  "4. Téléchargement illimité dans plusieurs formats",
                ].map((item, idx) => (
                  <h3
                    key={idx}
                    className="bg-white px-4 py-4 text-2xl font-bold rounded-lg"
                  >
                    {item}
                  </h3>
                ))}
              </div>
              <div className="w-full md:w-1/2 h-[250px] md:h-[350px] bg-blue-200 rounded-xl mt-6 md:mt-0"><SliderBackground/></div>
            </div>
            <button className="bg-[#14717D] font-bold text-white px-10 py-3 rounded-2xl text-lg hover:bg-blue-700 mt-10">
              Créer mon CV
            </button>
          </div>
        </section>

        {/* Bande décorative */}
       <section className="w-full h-150 bg-[#FAE7C3]"><div className="bg-gray-500 h-full w-full" style={{ borderRadius: "0px 100px 0px 0px" }}>
       <div className="flex items-center  flex-col text-[white]"> <h3 className="mt-4 text-5xl font-bold "><span className="text-[red]">35</span> modèles et <span className="text-[red]">25</span> couleurs</h3>
       <p className="text-xl mt-4">Tous nos modèles de CV sont approuvés par des recruteurs.</p></div>
        <div className="flex gap-2 p-8 "><div className="w-1/5 h-80 bg-cover "style={{ backgroundImage: "url('/cv1.jpeg')" }}></div><div className="w-1/5 h-80 bg-cover "style={{ backgroundImage: "url('/cv2.jpeg')" }}></div><div className="w-1/5 h-80 bg-cover "style={{ backgroundImage: "url('/cv3.jpeg')" }}></div><div className="w-1/5 h-80 bg-cover "style={{ backgroundImage: "url('/cv4.jpeg')" }}></div><div className="w-1/5 h-80 bg-cover "style={{ backgroundImage: "url('/cv5.jpeg')" }}></div></div>
          <div className="flex justify-center md:justify-start mt-4">
              <button className="bg-[#14717D] mx-auto font-bold text-white px-6 py-3 rounded-2xl text-lg hover:bg-blue-700">
                Decouvrir tous nos models
              </button>
            </div>
        </div></section>
        {/* Avis clients */}
        <section className="bg-gray-800 text-center px-4 py-12">
          <h3 className="text-3xl text-white font-bold mb-8">
            Ce que disent nos utilisateurs
          </h3>
          <div className="max-w-3xl mx-auto space-y-8 text-white">
            <blockquote className="italic">
              “Grâce à MonCVPro, j'ai trouvé un emploi en 2 semaines. Le CV
              était parfait.”<br />
              <span className="font-semibold text-gray-200">– Marie, Paris</span>
            </blockquote>
            <blockquote className="italic">
              “Facile à utiliser, rapide et super beau. Je recommande à tous
              mes amis.”<br />
              <span className="font-semibold text-gray-200">– Ahmed, Lyon</span>
            </blockquote>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className=" text-black py-6 px-4 text-center">
        <p>&copy; {new Date().getFullYear()} MonCVPro – Tous droits réservés</p>
        <div className="mt-2 space-x-4">
          <a href="#" className="underline">
            Mentions légales
          </a>
          <a href="#" className="underline">
            Contact
          </a>
        </div>
      </footer>
    </div>
  );
}
