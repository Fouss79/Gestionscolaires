'use client';
import React from "react";
import SliderBackground from "../component/Sliderbackground";
import Link from "next/link";

export default function HomePage() {
  const steps = [
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
  ];

  const cvImages = [
    "/cv1.jpeg",
    "/cv2.jpeg",
    "/cv3.jpeg",
    "/cv4.jpeg",
    "/cv5.jpeg",
  ];

  return (
    <div className="bg-white text-gray-800 font-sans">
      {/* Header */}
      <header className="flex justify-between items-center p-4 md:p-6 shadow-md">
        <h1 className="text-2xl font-bold text-gray-800 italic">
          Mon<span className="text-yellow-700">CVPro</span>
        </h1>
        <button className="text-gray-700 font-bold px-4 py-2 border rounded hover:bg-gray-100">
          Se connecter
        </button>
      </header>

      {/* Hero */}
      <main>
        <section className="flex flex-col md:flex-row">
          <div className="w-full md:w-1/2 h-auto md:h-[400px] text-center p-6 md:p-10 bg-gray-700 text-white rounded-b-[80px] md:rounded-none md:rounded-br-[80px] flex flex-col justify-center">
            <h2 className="text-2xl md:text-4xl font-bold mb-4">
              Créez un CV parfait en quelques minutes
            </h2>
            <p className="text-base md:text-lg mb-6">
              Des modèles professionnels et des conseils personnalisés pour vous aider à décrocher l'emploi de vos rêves.
            </p>
            <button className="bg-[#14717D] text-white px-6 py-3 rounded text-lg hover:bg-blue-700 mx-auto md:mx-0">
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
          <div className="bg-white rounded-tl-[80px] p-4 md:p-6">
            <h3 className="text-2xl md:text-5xl font-bold text-gray-600 text-center mt-4 mb-8 md:mb-12 italic">
              Comment ça fonctionne ?
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 md:gap-8 text-center">
              {steps.map((step, idx) => (
                <div key={idx} className="text-black px-2 md:px-4">
                  <div
                    className="bg-cover bg-center w-full h-[200px] md:h-[250px] rounded-xl mb-4"
                    style={{ backgroundImage: `url('${step.img}')` }}
                  ></div>
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

            <div className="flex justify-center mt-8 md:mt-12">
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
          className="bg-[#FAE7C3] mb-8 p-4 md:p-20 flex flex-col md:flex-row items-center gap-4 md:gap-6"
          style={{ borderRadius: "0px 80px 0px 80px" }}
        >
          <div className="w-full md:w-1/4 mb-4 md:mb-0">
            <div
              className="bg-cover w-full h-[180px] rounded-xl"
              style={{ backgroundImage: "url('/imcv.jpeg')" }}
            ></div>
          </div>
          <div className="w-full md:w-3/4 flex flex-col text-center md:text-left">
            <h1 className="text-xl md:text-3xl font-bold">
              Perfectionnez votre CV avec MonCVPro
            </h1>
            <p className="mt-4 text-sm md:text-base">
              Utilisez notre outil d'optimisation qui vérifie pour vous les 30 erreurs potentielles les plus communes sur un CV, et laissez-vous guider par les conseils et suggestions de contenus professionnels.
            </p>
            <div className="flex justify-center md:justify-start mt-6 md:mt-8">
              <button className="bg-[#14717D] hover:bg-blue-700 font-bold text-white px-6 py-3 rounded-2xl text-lg">
                Perfectionner mon CV
              </button>
            </div>
          </div>
        </section>

        {/* Mise en page */}
        <section className="w-full bg-gray-500">
          <div className="bg-[#FAE7C3] flex flex-col items-center px-4 md:px-20 py-8 md:py-10 rounded-bl-[100px]">
            <h1 className="text-2xl md:text-4xl font-bold text-gray-800 text-center mb-6 md:mb-10">
              Créez facilement un CV parfait grâce à nos outils de mise en page performants
            </h1>
            <div className="flex flex-col md:flex-row w-full gap-4 md:gap-6">
              <div className="w-full md:w-1/2 space-y-2 md:space-y-4">
                {[
                  "1. Modèle conçu pour les professionnels",
                  "2. Contenus pré-rédigés par des experts",
                  "3. Outil CV check",
                  "4. Téléchargement illimité dans plusieurs formats",
                ].map((item, idx) => (
                  <h3 key={idx} className="bg-white px-4 py-2 md:py-4 text-lg md:text-2xl font-bold rounded-lg">
                    {item}
                  </h3>
                ))}
              </div>
              <div className="w-full md:w-1/2 h-[200px] md:h-[350px] bg-blue-200 rounded-xl mt-4 md:mt-0">
                <SliderBackground />
              </div>
            </div>
            <button className="bg-[#14717D] font-bold text-white px-6 md:px-10 py-2 md:py-3 rounded-2xl text-lg hover:bg-blue-700 mt-6 md:mt-10">
              Créer mon CV
            </button>
          </div>
        </section>

        {/* Bande décorative */}
        <section className="w-full bg-[#FAE7C3] ">
          <div className="bg-gray-500 h-full w-full rounded-tl-[0px] rounded-tr-[100px] px-4 md:px-8 py-6">
            <div className="flex flex-col items-center text-white mb-4">
              <h3 className="text-3xl md:text-5xl font-bold">
                <span className="text-red-600">35</span> modèles et <span className="text-red-600">25</span> couleurs
              </h3>
              <p className="text-base md:text-xl mt-2 md:mt-4">
                Tous nos modèles de CV sont approuvés par des recruteurs.
              </p>
            </div>
            <div className="flex overflow-x-auto gap-2 p-2 md:p-8">
              {cvImages.map((img, idx) => (
                <div key={idx} className="flex-shrink-0 w-40 md:w-1/5 h-40 md:h-80 bg-cover rounded-xl" style={{ backgroundImage: `url('${img}')` }}></div>
              ))}
            </div>
            <div className="flex justify-center md:justify-start mt-4">
              <button className="bg-[#14717D] font-bold text-white px-6 py-2 md:py-3 rounded-2xl text-lg hover:bg-blue-700">
                Découvrir tous nos modèles
              </button>
            </div>
          </div>
        </section>

        {/* Avis clients */}
        <section className="bg-gray-800 text-center px-4 py-8 md:py-12">
          <h3 className="text-2xl md:text-3xl text-white font-bold mb-6 md:mb-8">
            Ce que disent nos utilisateurs
          </h3>
          <div className="max-w-3xl mx-auto space-y-4 md:space-y-8 text-white">
            <blockquote className="italic">
              “Grâce à MonCVPro, j'ai trouvé un emploi en 2 semaines. Le CV était parfait.”<br />
              <span className="font-semibold text-gray-200">– Marie, Paris</span>
            </blockquote>
            <blockquote className="italic">
              “Facile à utiliser, rapide et super beau. Je recommande à tous mes amis.”<br />
              <span className="font-semibold text-gray-200">– Ahmed, Lyon</span>
            </blockquote>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="text-black py-4 md:py-6 px-4 text-center">
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
