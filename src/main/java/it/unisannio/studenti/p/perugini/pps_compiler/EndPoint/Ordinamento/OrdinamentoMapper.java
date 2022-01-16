package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.Ordinamento;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;

public class OrdinamentoMapper {
    public static Ordinamento fromOrdinamentoDTOToOrdinamento(OrdinamentoDTO dto){
        Ordinamento ordinamento = new Ordinamento();
        ordinamento.setAnnoDiRedazione(dto.getAnnoDiRedazione());
        ordinamento.setCfuMassimiAScelta(dto.getCfuMassimiAScelta());
        ordinamento.setCfuMassimiObbligatori(dto.getCfuMassimiObbligatori());
        ordinamento.setCfuMassimiOrientamento(dto.getCfuMassimiOrientamento());
        ordinamento.setCfuMinimiAScelta(dto.getCfuMinimiAScelta());
        ordinamento.setCfuMinimiObbligatori(dto.getCfuMinimiObbligatori());
        ordinamento.setCfuMinimiOrientamento(dto.getCfuMinimiOrientamento());
        ordinamento.setCfuMassimiCorsoDiLaurea(dto.getCfuMassimiAScelta()+ dto.getCfuMassimiObbligatori()+ dto.getCfuMassimiOrientamento());
        ordinamento.setCfuMinimiCorsoDiLaurea(dto.getCfuMinimiAScelta()+dto.getCfuMinimiObbligatori()+dto.getCfuMinimiOrientamento());
        return ordinamento;
    }
}
