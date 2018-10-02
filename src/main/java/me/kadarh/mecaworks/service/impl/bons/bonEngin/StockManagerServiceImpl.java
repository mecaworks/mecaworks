package me.kadarh.mecaworks.service.impl.bons.bonEngin;

import lombok.extern.slf4j.Slf4j;
import me.kadarh.mecaworks.domain.others.Chantier;
import me.kadarh.mecaworks.domain.others.Stock;
import me.kadarh.mecaworks.domain.others.TypeBon;
import me.kadarh.mecaworks.repo.others.StockRepo;
import me.kadarh.mecaworks.service.ChantierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * PROJECT mecaworks
 *
 * @author kadarH
 * Created at 27/09/18
 */

@Service
@Transactional
@Slf4j
public class StockManagerServiceImpl {

    private final StockRepo stockRepo;
    private final ChantierService chantierService;

    public StockManagerServiceImpl(StockRepo stockRepo, ChantierService chantierService) {
        this.stockRepo = stockRepo;
        this.chantierService = chantierService;
    }

    public void deleteStock(Long idC_gasoil, Long idC_travail, Long id_bon, TypeBon type_bon) {
        //verify date stock and date dernier inventaire
        //if date_stock > date_dernier_inventaire
        //then doUpdateStocks + doUpdateChantierStock
        //else don't do anything
        //always do : delete stock with id bon = id_bon and type bon = type_bon
        LocalDate date;
        Stock stock = stockRepo.findByTypeBonAndId_Bon(type_bon, id_bon).get();
        List<Stock> list = stockRepo.findAllById_Bon(id_bon);
        stockRepo.deleteAll(list);
        if (type_bon.equals(TypeBon.BE) && weHaveToDoMiseAjour(stock.getDate(), idC_travail)) {
            doMiseAjour(idC_travail, stock, true);
            updateStockChantier(idC_travail, stock.getQuantite(), true);
        }
        if (type_bon.equals(TypeBon.BF) && weHaveToDoMiseAjour(stock.getDate(), idC_travail)) {
            doMiseAjour(idC_travail, stock, false);
            updateStockChantier(idC_travail, stock.getQuantite(), false);
        }
        if (type_bon.equals(TypeBon.BL)) {
            if (weHaveToDoMiseAjour(stock.getDate(), idC_gasoil)) {
                doMiseAjour(idC_travail, stock, true);
                updateStockChantiers(idC_gasoil, idC_travail, stock.getQuantite());
            }
            if (weHaveToDoMiseAjour(stock.getDate(), idC_travail)) {
                doMiseAjour(idC_travail, stock, false);
                updateStockChantiers(idC_gasoil, idC_travail, stock.getQuantite());
            }
        }
    }

    public void addStockMiseAjour(Long idC_travail, Long idC_gasoil, Stock stock, TypeBon type_bon) {
        if (type_bon.equals(TypeBon.BE) && weHaveToDoMiseAjour(stock.getDate(), idC_travail)) {
            doMiseAjour(idC_travail, stock, false);
        }
        if (type_bon.equals(TypeBon.BF) && weHaveToDoMiseAjour(stock.getDate(), idC_travail)) {
            doMiseAjour(idC_travail, stock, true);
        }
        if (type_bon.equals(TypeBon.BL)) {
            if (weHaveToDoMiseAjour(stock.getDate(), idC_gasoil)) {
                doMiseAjour(idC_travail, stock, true);
            }
            if (weHaveToDoMiseAjour(stock.getDate(), idC_travail)) {
                doMiseAjour(idC_travail, stock, false);
            }
        }
    }


    private boolean weHaveToDoMiseAjour(LocalDate dateStock, Long idC) {
        //get date of dernier mise a jour
        //compare them
        Optional<Stock> stock = stockRepo.findLastStockReel(idC);
        return !stock.isPresent() || stock.get().getDate().isBefore(dateStock);
    }

    private void doMiseAjour(Long idc, Stock stock, boolean signe) {
        Optional<Stock> stockx = stockRepo.findLastStockReel(idc);
        List<Stock> list = stockRepo.findAllByChantierAndIdGreaterThan(chantierService.get(idc), stock.getId());
        list.forEach(stock1 -> stock1.setStockC(signe ? stock1.getStockC() + stock.getQuantite() : stock1.getStockC() - stock.getQuantite()));
        stockRepo.saveAll(list);
    }

    private void updateStockChantiers(Long idC_gasoil, Long idC_travail, Integer quantite) {
        updateStockChantier(idC_gasoil, quantite, true);
        updateStockChantier(idC_travail, quantite, false);
    }

    private void updateStockChantier(Long idc, Integer quantite, boolean signe) {
        Chantier chantier = chantierService.get(idc);
        chantier.setStock(signe ? chantier.getStock() + quantite : chantier.getStock() - quantite);
        chantierService.update(chantier);
    }

}