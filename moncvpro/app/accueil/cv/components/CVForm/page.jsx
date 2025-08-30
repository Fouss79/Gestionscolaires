"use client";

import React, { useState } from "react";

export default function CVForm({ onSubmit, onReset }) {
  
  const [formData, setFormData] = useState({
    nom: "",
    titre: "",
    profil: "",
    photo: "",
    contact: {
      telephone: "",
      email: "",
      adresse: "",
    },
    competences: [""],
    langues: [""],
    interets: [""],
    formations: [{ diplome: "", ecole: "", annee: "" }],
    experiences: [{ poste: "", entreprise: "", dates: "", responsabilites: [""] }],
  });

  const handleChange = (e, field = null, index = null, subfield = null) => {
    const { name, value } = e.target;
    const updated = { ...formData };

    if (field && index !== null) {
      updated[field] = [...formData[field]];
      if (subfield) {
        updated[field][index][subfield] = value;
      } else {
        updated[field][index] = value;
      }
    } else if (name.startsWith("contact.")) {
      const key = name.split(".")[1];
      updated.contact[key] = value;
    } else {
      updated[name] = value;
    }

    setFormData(updated);
  };

  const addItem = (field, template) => {
    setFormData({ ...formData, [field]: [...formData[field], template] });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const resetForm = () => {
    setFormData({
      nom: "",
      titre: "",
      profil: "",
      photo: "",
      contact: {
        telephone: "",
        email: "",
        adresse: "",
      },
      competences: [""],
      langues: [""],
      interets: [""],
      formations: [{ diplome: "", ecole: "", annee: "" }],
      experiences: [{ poste: "", entreprise: "", dates: "", responsabilites: [""] }],
    });
    onReset?.();
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6 max-w-3xl mx-auto p-4">
      <h2 className="text-xl font-bold">Informations personnelles</h2>
      <input name="nom" placeholder="Nom Prénom" value={formData.nom} onChange={handleChange} className="input" />
      <input name="titre" placeholder="Intitulé du poste" value={formData.titre} onChange={handleChange} className="input" />
      <textarea name="profil" placeholder="Profil professionnel" value={formData.profil} onChange={handleChange} className="textarea" />

      <h3 className="font-semibold">Photo de profil</h3>
      <input type="file" accept="image/*" onChange={(e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onloadend = () => {
            setFormData({ ...formData, photo: reader.result });
          };
          reader.readAsDataURL(file);
        }
      }} className="input" />
      {formData.photo && (
        <img src={formData.photo} alt="Aperçu" className="mt-2 rounded w-32 h-32 object-cover" />
      )}

      <h3 className="font-semibold">Contact</h3>
      <input name="contact.telephone" placeholder="Téléphone" value={formData.contact.telephone} onChange={handleChange} className="input" />
      <input name="contact.email" placeholder="Email" value={formData.contact.email} onChange={handleChange} className="input" />
      <input name="contact.adresse" placeholder="Adresse" value={formData.contact.adresse} onChange={handleChange} className="input" />

      <h3 className="font-semibold">Compétences</h3>
      {formData.competences.map((c, i) => (
        <input key={i} value={c} onChange={(e) => handleChange(e, "competences", i)} className="input" />
      ))}
      <button type="button" onClick={() => addItem("competences", "")} className="btn">+ Ajouter compétence</button>

      <h3 className="font-semibold">Langues</h3>
      {formData.langues.map((l, i) => (
        <input key={i} value={l} onChange={(e) => handleChange(e, "langues", i)} className="input" />
      ))}
      <button type="button" onClick={() => addItem("langues", "")} className="btn">+ Ajouter langue</button>
       <h3 className="font-semibold">Centres d’intérêt</h3>
{formData.interets.map((interest, i) => (
  <input
    key={i}
    value={interest}
    onChange={(e) => handleChange(e, "interets", i)}
    className="input"
  />
))}
<button
  type="button"
  onClick={() => addItem("interets", "")}
  className="btn"
>
  + Ajouter centre d’intérêt
</button>

      <h3 className="font-semibold">Formations</h3>
      {formData.formations.map((f, i) => (
        <div key={i} className="grid grid-cols-3 gap-2">
          <input placeholder="Diplôme" value={f.diplome} onChange={(e) => handleChange(e, "formations", i, "diplome")} className="input" />
          <input placeholder="École" value={f.ecole} onChange={(e) => handleChange(e, "formations", i, "ecole")} className="input" />
          <input placeholder="Année" value={f.annee} onChange={(e) => handleChange(e, "formations", i, "annee")} className="input" />
        </div>
      ))}
      <button type="button" onClick={() => addItem("formations", { diplome: "", ecole: "", annee: "" })} className="btn">+ Ajouter formation</button>

      <h3 className="font-semibold">Expériences</h3>
      {formData.experiences.map((exp, i) => (
        <div key={i} className="space-y-1 border p-3 rounded">
          <input placeholder="Poste" value={exp.poste} onChange={(e) => handleChange(e, "experiences", i, "poste")} className="input" />
          <input placeholder="Entreprise" value={exp.entreprise} onChange={(e) => handleChange(e, "experiences", i, "entreprise")} className="input" />
          <input placeholder="Dates" value={exp.dates} onChange={(e) => handleChange(e, "experiences", i, "dates")} className="input" />
          <p className="text-sm font-medium mt-2">Responsabilités</p>
          {exp.responsabilites.map((r, j) => (
            <input key={j} value={r} onChange={(e) => {
              const updatedExp = [...formData.experiences];
              updatedExp[i].responsabilites[j] = e.target.value;
              setFormData({ ...formData, experiences: updatedExp });
            }} className="input" />
          ))}
          <button type="button" onClick={() => {
            const updatedExp = [...formData.experiences];
            updatedExp[i].responsabilites.push("");
            setFormData({ ...formData, experiences: updatedExp });
          }} className="btn mt-1">+ Ajouter tâche</button>
        </div>
      ))}
      <button type="button" onClick={() => addItem("experiences", { poste: "", entreprise: "", dates: "", responsabilites: [""] })} className="btn">+ Ajouter expérience</button>

      <div className="flex justify-between mt-6">
        <button type="submit" className="btn-primary">Prévisualiser le CV</button>
        <button type="button" onClick={resetForm} className="btn">Réinitialiser</button>
      </div>
    </form>
  );
}
