package me.kadarh.mecaworks.gazoil.domain.alertes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.kadarh.mecaworks.gazoil.config.LocalDateConverter;
import me.kadarh.mecaworks.gazoil.domain.AbstractDomain;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * @author kadarH
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class AlerteEngin extends AbstractDomain {

    @Convert(converter = LocalDateConverter.class)
    private LocalDate date;
    private TypeAlerte typeAlerte;
    private String message;
    private boolean etat;
    private float consommationKm;
    private float consommationH;

    private Long idBon;

}
