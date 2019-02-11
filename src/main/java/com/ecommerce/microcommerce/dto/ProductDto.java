package com.ecommerce.microcommerce.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.hibernate.validator.constraints.Length;
import javax.persistence.Id;

/**
 *
 * @author openClassRhum
 */


/*
  On utilise des objets transverses à la place des entités/dao
  dans le métier et le front-end
  Dto copie de l'entité Product.java
  On ajoute un attribut marge en plus par rapport à l'entité originale
*/
@JsonFilter("monFiltreDynamiqueDto")
public class ProductDto {

    @Id
    private int id;

    @Length(min=3, max=20, message = "Nom trop long ou trop court. Et oui messages sont plus stylés que ceux de Spring")
    private String nom;
    
    private int prix;

    
    private int prixAchat;
    
    private int marge ;

    //constructeur par défaut
    public ProductDto() {
    }

    public ProductDto(int id, String nom, int prix, int prixAchat) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.prixAchat = prixAchat;
        //this.marge = prix - prixAchat;
    }

    @Override
    public String toString() {
        return "ProductDto{" + "id=" + id + ", nom=" + nom + ", prix=" + prix + ", prixAchat=" + prixAchat + ", marge=" + marge + '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(int prixAchat) {
        this.prixAchat = prixAchat;
    }

    public int getMarge() {
        return this.getPrix()-this.getPrixAchat();
    }

    public void setMarge(int marge) {
        this.marge = marge;
    }




 


}
