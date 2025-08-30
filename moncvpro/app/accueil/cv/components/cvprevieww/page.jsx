'use client';
import React from "react";
import CVPreviewModern from "../CVprev/CVPreviewModern";
import CVPreviewElegant from "../CVprev/CVPreviewElegant";
import CVPreviewA4CompactFull from "../CVPreviewA4CompactFull";

// Assure-toi que ces fichiers existent bien dans /app/accueil/cvprev/


export default function CVPreview({ data, template }) {
  switch (template) {
    case 2:
      return <CVPreviewModern data={data}/>;
    case 3:
      return <CVPreviewElegant data={data}/>;
    default:
      return <CVPreviewA4CompactFull data={data}/>;
  }
}
