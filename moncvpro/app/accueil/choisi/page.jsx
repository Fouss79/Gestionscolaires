"use client";
import React, { useState, useRef } from "react";
import CVForm from "./components/CVForm/page";
import CVPreviewMinimal from "./components/CVprev/CVPreviewMinimal";
import TemplateSelector from "./components/templateSelector";
import CVPreviewModern from "./components/CVprev/CVPreviewModern";
import CVPreviewElegant from "./components/CVprev/CVPreviewElegant";
import CVPreviewA4CompactFull from "./components/CVPreviewA4CompactFull";
import CVPreviewTwoColumns from "./components/CVprev/CVPreviewTwoColumns";
import CVPreviewCreative from "./components/CVprev/CVPreviewCreative";
import CouleurSelector from "./components/CVprev/CouleurSelector";

export default function CVPage() {

   const initialData = {
    nom: "Jean Dupont",
    titre: "Développeur Full Stack",
    profil:
      "Passionné par le développement web, avec une solide expérience en React et Spring Boot. Je cherche à rejoindre une équipe dynamique où je pourrai contribuer à des projets innovants.",
    photo: "/maphoto.jpeg",
    contact: {
      telephone: "+33 6 12 34 56 78",
      email: "jean.dupont@example.com",
      adresse: "12 rue des Lilas, 75015 Paris",
    },
    competences: [
      "React",
      "Next.js",
      "Spring Boot",
      "Tailwind CSS",
      "MySQL",
      "Docker",
      "Git/GitHub",
    ],
    langues: ["Français (natif)", "Anglais (courant)", "Espagnol (intermédiaire)"],
    interets: ["Voyages", "Lecture", "Football", "Photographie"],
    formations: [
      { diplome: "Master Informatique", ecole: "Université de Paris", annee: "2022" },
      { diplome: "Licence Informatique", ecole: "Université de Lyon", annee: "2020" },
    ],
    experiences: [
      {
        poste: "Développeur Full Stack",
        entreprise: "TechCorp",
        dates: "2022 - Présent",
        responsabilites: [
          "Développement d'applications web avec React et Spring Boot",
          "Mise en place de pipelines CI/CD avec GitHub Actions",
          "Optimisation des performances front-end",
        ],
      },
      {
        poste: "Stagiaire Développeur Web",
        entreprise: "StartupWeb",
        dates: "Janv 2021 - Juin 2021",
        responsabilites: [
          "Création d'une interface utilisateur en React",
          "Intégration d'API REST",
          "Amélioration de l’expérience utilisateur",
        ],
      },
    ],
  };
  
   const [defaultData, setDefaultData] = useState(initialData);
  const [cvData, setCvData] = useState(initialData);
  const [couleurChoisie, setCouleurChoisie] = useState("#3b82f6");
  const [selectedTemplate, setSelectedTemplate] = useState(1);
  const previewRef = useRef(null);

  const generatePDF = async () => {
    const element = document.getElementById("cv-preview");

    if (!element) return;

    // ✅ Import dynamique côté client uniquement
    const html2pdf = (await import("html2pdf.js")).default;

    const opt = {
      margin: [10, 10, 10, 10],
      filename: "mon_cv.pdf",
      image: { type: "jpeg", quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: "mm", format: "a4", orientation: "portrait" },
      pagebreak: { mode: ["avoid-all", "css", "legacy"] },
    };

    html2pdf().set(opt).from(element).save();
  };

   const handleSubmit = (data) => {
    setCvData(data);          // met à jour le CV courant
    setDefaultData(data);     // met aussi à jour le "defaultData"
  };
  const handleReset = () => setCvData(null);

  const renderCVPreview = () => {
    switch (selectedTemplate) {
      case 1:
        return <CVPreviewModern couleur={couleurChoisie} data={cvData} />;
      case 2:
        return <CVPreviewElegant couleur={couleurChoisie} data={cvData} />;
      case 3:
        return <CVPreviewMinimal couleur={couleurChoisie} data={cvData} />;
      case 4:
        return <CVPreviewA4CompactFull couleur={couleurChoisie} data={cvData} />;
      case 5:
        return <CVPreviewTwoColumns couleur={couleurChoisie} data={cvData} />;
      case 6:
        return <CVPreviewCreative couleur={couleurChoisie} data={cvData} />;
      default:
        return <CVPreviewModern couleur={couleurChoisie} data={cvData} />;
    }
  };

  return (
    <div className="min-h-screen flex flex-col md:flex-row gap-8 p-4">
      {/* Formulaire */}
      <div className="w-1/2 bg-gray-100">
        <CVForm onSubmit={handleSubmit} onReset={handleReset} />
      </div>

      {/* Preview + options */}
      <div className=" bg-gray-100 rounded shadow p-4">
        <div
          id="cv-preview"
          ref={previewRef}
          className="  mb-6 border bg-white rounded shadow"
        >
          {
            renderCVPreview()
          }
        </div>

        <h2 className="text-lg font-bold mb-2">Choisis une couleur :</h2>
        <div className="flex justify-end">
          <CouleurSelector
            selectedColor={couleurChoisie}
            onSelect={setCouleurChoisie}
          />
        </div>

        {
          <>
            <TemplateSelector
              selected={selectedTemplate}
              onSelect={setSelectedTemplate}
              cvData={cvData}
              couleurChoisie={couleurChoisie}
            />

            <button
              className="mt-4 px-4 py-2 rounded transition text-white"
              style={{ backgroundColor: couleurChoisie }}
              onClick={generatePDF}
            >
              Télécharger en PDF
            </button>
          </>
        }
      </div>
    </div>
  );
}
