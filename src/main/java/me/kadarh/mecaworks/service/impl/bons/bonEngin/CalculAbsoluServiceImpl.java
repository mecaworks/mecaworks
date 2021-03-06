package me.kadarh.mecaworks.service.impl.bons.bonEngin;

import lombok.extern.slf4j.Slf4j;
import me.kadarh.mecaworks.domain.bons.BonEngin;
import me.kadarh.mecaworks.domain.others.TypeCompteur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kadarH
 */

@Service
@Slf4j
@Transactional
public class CalculAbsoluServiceImpl {

    private final FillServiceImpl fillService;
    private PersistServiceImpl persistService;

    public CalculAbsoluServiceImpl(PersistServiceImpl persistService, FillServiceImpl fillService) {
        this.persistService = persistService;
        this.fillService = fillService;
    }

    public BonEngin fillBon(BonEngin bon) {
        bon = fillService.fillBonEnginPart1(bon);
        bon = calculCompteursAbsolu(bon, persistService.getLastBonEngin(bon.getEngin(),bon.getDate()));
        bon = fillService.fillBonEnginPart2(bon);
        return bon;
    }

    private BonEngin calculCompteursAbsolu(BonEngin bonEngin, BonEngin lastBon) {
        log.info("- > Calcul compteur Absolu");
        String typeCompteur = bonEngin.getEngin().getSousFamille().getTypeCompteur().name();
        BonEngin bonEngin1 = lastBon;
        if (typeCompteur.equals(TypeCompteur.H.name())) {
            bonEngin = calculCompteurAbsoluH(bonEngin, bonEngin1);
            bonEngin.setCompteurKm(0L);
        } else if (typeCompteur.equals(TypeCompteur.KM.name())) {
            bonEngin = calculCompteurAbsoluKm(bonEngin, bonEngin1);
            bonEngin.setCompteurH(0L);
        } else if (typeCompteur.equals(TypeCompteur.KM_H.name())) {
            bonEngin = calculCompteurAbsoluH_Km(bonEngin, bonEngin1);
        }
        log.info("--- > Calcul compteur AbsoluKm = " + bonEngin.getCompteurKm());
        log.info("--- > Calcul compteur AbsoluH = " + bonEngin.getCompteurH());
        log.info("------------------------------------------ Compteur absolu calculated ------");
        return bonEngin;
    }

    private BonEngin calculCompteurAbsoluH_Km(BonEngin bonEngin, BonEngin bonEngin1) {
        log.info("--- > Calcul compteur Absolu H et Km");
        if (bonEngin1 != null) {
            bonEngin = verifyBon_ifCompteurH_EnPanne(bonEngin, bonEngin1);
            bonEngin = setCompteurAbsoluH_ifCmpBonInfCmptLastBon(bonEngin, bonEngin1);
            bonEngin = verifyBon_ifCompteurKm_EnPanne(bonEngin, bonEngin1);
            bonEngin = setCompteurAbsoluKm_ifCmpBonInfCmptLastBon(bonEngin, bonEngin1);
        } else {
            bonEngin.setNbrHeures(0L);
            bonEngin.setNbrKm(0L);
            bonEngin.setCompteurAbsoluH(bonEngin.getCompteurH());
            bonEngin.setCompteurAbsoluKm(bonEngin.getCompteurKm());
        }
        return bonEngin;
    }

    private BonEngin calculCompteurAbsoluH(BonEngin bonEngin, BonEngin bonEngin1) {
        log.info("--- > Calcul compteur Absolu H");
        if (bonEngin1 != null) {
            bonEngin = verifyBon_ifCompteurH_EnPanne(bonEngin, bonEngin1);
            setCompteurAbsoluH_ifCmpBonInfCmptLastBon(bonEngin, bonEngin1);
        } else {
            bonEngin.setCompteurAbsoluH(bonEngin.getCompteurH());
            bonEngin.setNbrHeures(0L);
        }
        return bonEngin;
    }

    private BonEngin calculCompteurAbsoluKm(BonEngin bonEngin, BonEngin bonEngin1) {
        log.info("--- > Calcul compteur Absolu Km");
        if (bonEngin1 != null) {
            bonEngin = verifyBon_ifCompteurKm_EnPanne(bonEngin, bonEngin1);
            setCompteurAbsoluKm_ifCmpBonInfCmptLastBon(bonEngin, bonEngin1);
        } else {
            bonEngin.setCompteurAbsoluKm(bonEngin.getCompteurKm());
            bonEngin.setNbrKm(0L);
        }
        return bonEngin;
    }

    private BonEngin verifyBon_ifCompteurH_EnPanne(BonEngin bonEngin, BonEngin bonEngin1) {
        log.info("- > Verification compteur H");
        if (bonEngin.getCompteurHenPanne()) {
            log.info("--- > Calcul H en panne");
            bonEngin.setCompteurH(bonEngin1.getCompteurH());
            bonEngin.setCompteurAbsoluH(bonEngin1.getCompteurAbsoluH());
            bonEngin.setNbrHeures(0L);
        } else {
            log.info("--- > Calcul H à marche");
            bonEngin.setCompteurAbsoluH(bonEngin1.getCompteurAbsoluH() + bonEngin.getCompteurH() - bonEngin1.getCompteurH());
            bonEngin.setNbrHeures(bonEngin.getCompteurH() - bonEngin1.getCompteurH());
        }
        return bonEngin;
    }

    private BonEngin verifyBon_ifCompteurKm_EnPanne(BonEngin bonEngin, BonEngin bonEngin1) {
        log.info("- > Verification compteur Km");
        if (bonEngin.getCompteurKmenPanne()) {
            log.info("--- > Calcul Km en panne");
            bonEngin.setCompteurKm(bonEngin1.getCompteurKm());
            bonEngin.setCompteurAbsoluKm(bonEngin1.getCompteurAbsoluKm());
            bonEngin.setNbrKm(0L);
        } else {
            log.info("--- > Calcul Km à marche");
            bonEngin.setCompteurAbsoluKm(bonEngin1.getCompteurAbsoluKm() + bonEngin.getCompteurKm() - bonEngin1.getCompteurKm());
            bonEngin.setNbrKm(bonEngin.getCompteurKm() - bonEngin1.getCompteurKm());
        }
        return bonEngin;
    }

    private BonEngin setCompteurAbsoluH_ifCmpBonInfCmptLastBon(BonEngin bonEngin, BonEngin bonEngin1) {
        if (bonEngin.getCompteurH() < bonEngin1.getCompteurH()) {
            if (bonEngin.getCompteurHenPanne()) {
                bonEngin.setCompteurAbsoluH(bonEngin1.getCompteurAbsoluH());
                bonEngin.setCompteurH(bonEngin1.getCompteurH());
                bonEngin.setNbrHeures(0L);
            } else {
                bonEngin.setCompteurAbsoluH(bonEngin1.getCompteurAbsoluH() + bonEngin.getCompteurH());
                bonEngin.setNbrHeures(bonEngin.getCompteurH());
            }
        }
        return bonEngin;
    }

    private BonEngin setCompteurAbsoluKm_ifCmpBonInfCmptLastBon(BonEngin bonEngin, BonEngin bonEngin1) {
        if (bonEngin.getCompteurKm() < bonEngin1.getCompteurKm()) {
            if (bonEngin.getCompteurKmenPanne()) {
                bonEngin.setCompteurAbsoluKm(bonEngin1.getCompteurAbsoluKm());
                bonEngin.setCompteurKm(bonEngin1.getCompteurKm());
                bonEngin.setNbrKm(bonEngin1.getCompteurKm());
            } else
                bonEngin.setCompteurAbsoluKm(bonEngin1.getCompteurAbsoluKm() + bonEngin.getCompteurKm());
            bonEngin.setNbrKm(bonEngin.getCompteurKm());
            }
        return bonEngin;
    }
}