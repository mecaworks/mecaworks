package me.kadarh.mecaworks.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * PROJECT mecaworks
 *
 * @author kadarH
 * Created at 30/05/18
 */
@Data
public class Dashbord {

	private List<ChantierBatch> chantierBatch = new ArrayList<>();
	private List<Quantite> quantites = new ArrayList<>();

	@JsonIgnore
	public Long getTotaleGazoile() {
		return quantites.stream()
				.mapToLong(Quantite::getQuantity)
				.sum() - quantites.get(0).getQuantity();
	}

	@JsonIgnore
	public Double getTotaleGazoileDh() {
		return quantites.stream()
				.mapToDouble(quantite -> quantite.getQuantity() * quantite.getPrix())
				.sum() - quantites.get(0).getQuantity() * quantites.get(0).getPrix();
	}

	@JsonIgnore
	public Long getTotaleGazoileLocation() {
		return quantites.stream()
				.mapToLong(Quantite::getQuantiteLocation)
				.sum() - quantites.get(0).getQuantiteLocation();
	}

	@JsonIgnore
	public Double getTotaleGazoileLocationDh() {
		return quantites.stream()
				.mapToDouble(quantite -> quantite.getQuantiteLocation() * quantite.getPrix())
				.sum() - quantites.get(0).getQuantiteLocation() * quantites.get(0).getPrix();
	}

	@JsonIgnore
	public Long getTotaleGazoileInterne() {
		return quantites.stream()
				.mapToLong(q -> q.getQuantity() - q.getQuantiteLocation())
				.sum() - (quantites.get(0).getQuantity() - quantites.get(0).getQuantiteLocation());
	}

	@JsonIgnore
	public Double getTotaleGazoileInterneDh() {
		return quantites.stream()
				.mapToDouble(q -> (q.getQuantity() - q.getQuantiteLocation()) * q.getPrix())
				.sum() - (quantites.get(0).getQuantity() - quantites.get(0).getQuantiteLocation()) * quantites.get(0).getPrix();
	}

	@JsonIgnore
	public Long getConsommationTotalePrevue() {
		return quantites.stream()
				.mapToLong(Quantite::getConsommationPrevue)
				.sum() - quantites.get(0).getConsommationPrevue();
	}

	@JsonIgnore
	public Double getConsommationTotalePrevueDh() {
		return quantites.stream()
				.mapToDouble(q -> q.getConsommationPrevue() * q.getPrix())
				.sum() - quantites.get(0).getConsommationPrevue() * quantites.get(0).getPrix();
	}

	@JsonIgnore
	public Long getChargeLocataireTotale() {
		return quantites.stream()
				.mapToLong(Quantite::getChargeLocataire)
				.sum() - quantites.get(0).getChargeLocataire();
	}

	@JsonIgnore
	public Long getChargeLocataireExterne() {
		return quantites.stream()
				.mapToLong(Quantite::getChargeLocataireExterne)
				.sum() - quantites.get(0).getChargeLocataireExterne();
	}

	@JsonIgnore
	public Long getChargeLocataireInterne() {
		return quantites.stream()
				.mapToLong(quantite -> quantite.getChargeLocataire() - quantite.getChargeLocataireExterne())
				.sum() - (quantites.get(0).getChargeLocataire() - quantites.get(0).getChargeLocataireExterne());
	}

	@JsonIgnore
	public Long getGazoilAcheteTotal() {
		return quantites.stream()
				.mapToLong(Quantite::getGazoilAchete)
				.sum() - quantites.get(0).getGazoilAchete();
	}

	@JsonIgnore
	public Double getGazoilAcheteTotalDh() {
		return quantites.stream()
				.mapToDouble(q -> q.getGazoilAchete() * q.getPrix())
				.sum() - quantites.get(0).getGazoilAchete() * quantites.get(0).getPrix();
	}

	@JsonIgnore
	public Long getGazoilFlotantTotal() {
		return quantites.stream()
				.mapToLong(Quantite::getGazoilFlotant)
				.sum() - quantites.get(0).getGazoilFlotant();
	}

	@JsonIgnore
	public Double getGazoilFlotantTotalDh() {
		return quantites.stream()
				.mapToDouble(q -> q.getGazoilFlotant() * q.getPrix())
				.sum() - quantites.get(0).getGazoilFlotant() * quantites.get(0).getPrix();
	}
}
