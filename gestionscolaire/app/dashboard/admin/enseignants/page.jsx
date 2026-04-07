"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../../../context/AuthContext";

export default function EnseignantPage() {
  const { user } = useAuth();

  const [form, setForm] = useState({
    nom: "",
    prenom: "",
    telephone: "",
    specialite: "",
  });

  const [enseignants, setEnseignants] = useState([]);

  const loadEnseignants = async () => {
    if (!user?.ecole?.id) return;

    try {
      const res = await axios.get(
        `http://localhost:8080/api/enseignants/ecole/${user.ecole.id}`
      );
      setEnseignants(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadEnseignants();
  }, [user]);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await axios.post("http://localhost:8080/api/enseignants", {
        ...form,
        ecoleId: user.ecole.id,
      });

      setForm({
        nom: "",
        prenom: "",
        telephone: "",
        specialite: "",
      });

      loadEnseignants();
      alert("✅ Enseignant ajouté !");
    } catch (err) {
      console.error(err);
      alert("Erreur !");
    }
  };

  const handleDelete = async (id) => {
    if (!confirm("Supprimer cet enseignant ?")) return;

    try {
      await axios.delete(`http://localhost:8080/api/enseignants/${id}`);
      loadEnseignants();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">

      {/* GRID */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

        {/* ================= FORM ================= */}
        <form
          onSubmit={handleSubmit}
          className="bg-white p-6 rounded-2xl shadow-md space-y-4 md:col-span-1 h-fit"
        >
          <h2 className="text-xl font-semibold text-gray-700">
            Ajouter un enseignant
          </h2>

          <input
            type="text"
            name="nom"
            placeholder="Nom"
            value={form.nom}
            onChange={handleChange}
            className="border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none p-3 w-full rounded-lg"
            required
          />

          <input
            type="text"
            name="prenom"
            placeholder="Prénom"
            value={form.prenom}
            onChange={handleChange}
            className="border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none p-3 w-full rounded-lg"
            required
          />

          <input
            type="text"
            name="telephone"
            placeholder="Téléphone"
            value={form.telephone}
            onChange={handleChange}
            className="border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none p-3 w-full rounded-lg"
          />

          <input
            type="text"
            name="specialite"
            placeholder="Spécialité (ex: Math)"
            value={form.specialite}
            onChange={handleChange}
            className="border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none p-3 w-full rounded-lg"
          />

          <button className="w-full bg-blue-600 hover:bg-blue-700 transition text-white px-5 py-2 rounded-lg shadow">
            Ajouter
          </button>
        </form>

        {/* ================= TABLE ================= */}
        <div className="bg-white p-6 rounded-2xl shadow-md md:col-span-2">

          <h2 className="text-xl font-semibold text-gray-700 mb-4">
            Liste des enseignants
          </h2>

          <div className="overflow-x-auto">
            <table className="w-full text-sm border-collapse">
              <thead>
                <tr className="bg-gray-100 text-gray-600">
                  <th className="p-3 text-left">Nom</th>
                  <th className="p-3 text-left">Prénom</th>
                  <th className="p-3 text-left">Téléphone</th>
                  <th className="p-3 text-left">Spécialité</th>
                  <th className="p-3 text-left">Actions</th>
                </tr>
              </thead>

              <tbody>
                {enseignants.map((e, index) => (
                  <tr
                    key={e.id}
                    className={`border-t hover:bg-gray-50 ${
                      index % 2 === 0 ? "bg-white" : "bg-gray-50"
                    }`}
                  >
                    <td className="p-3">{e.nom}</td>
                    <td className="p-3">{e.prenom}</td>
                    <td className="p-3">{e.telephone}</td>
                    <td className="p-3">{e.specialite}</td>

                    <td className="p-3 flex gap-2">
                      <button
                        className="bg-yellow-500 hover:bg-yellow-600 transition text-white px-3 py-1 rounded-lg"
                        onClick={() => alert("Modifier à faire")}
                      >
                        Modifier
                      </button>

                      <button
                        className="bg-red-600 hover:bg-red-700 transition text-white px-3 py-1 rounded-lg"
                        onClick={() => handleDelete(e.id)}
                      >
                        Supprimer
                      </button>
                    </td>
                  </tr>
                ))}

                {enseignants.length === 0 && (
                  <tr>
                    <td
                      colSpan="5"
                      className="text-center p-4 text-gray-400"
                    >
                      Aucun enseignant
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

        </div>
      </div>
    </div>
  );
}