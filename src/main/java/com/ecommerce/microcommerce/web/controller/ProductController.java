package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.dto.ProductDto;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

/**
 *
 * @author openClassRhum
 */


@Api(description = "API pour les opérations CRUD sur les produits.")
@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;

    //Récupérer la liste des produits
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)

    public MappingJacksonValue listeProduits() {

        Iterable<Product> produits = productDao.findAll();
        //on mappe notre entité en dto
        //on ne renvoie jamais une entité au front-end !!!!!!!
        ModelMapper modelMapper = new ModelMapper();
        List<ProductDto> produitsDto = modelMapper.map(produits, new TypeToken<List<ProductDto>>() {
        }.getType());

        //mise en place d'un filtre
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "marge");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamiqueDto", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produitsDto);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }

    
    
    
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")
    public MappingJacksonValue afficherUnProduit(@PathVariable int id) {

        Product produit = productDao.findById(id);
        //on mappe notre entité en dto
        //on ne renvoie jamais une entité au front-end !!!!!!!
        ModelMapper modelMapper = new ModelMapper();
        ProductDto produitDto = modelMapper.map(produit, new TypeToken<ProductDto>() {
        }.getType());

        if (produitDto == null) {
            throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");
        }

        //on crée un filtre, on ne remonte pas la marge et le prix d'achat
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "marge");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamiqueDto", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produitDto);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }

    
    
    /*ajouter un produit
        Aucune vérification n'est faite afin de savoir si l'objet est déjà présent
        en bdd
        TODO dans un prochain cours ;)
    */
    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody ProductDto productDto) {

        //on teste si l'utilisateur a transmis un prix
        if (productDto.getPrix() == 0) {
            //on renvoie le code 422 ==> entité incomplète
            throw new ProduitGratuitException("Produit incomplet==> le prix est manquant :) ");
        }

        //on mappe la dto en entité
        ModelMapper modelMapper = new ModelMapper();
        Product product = modelMapper.map(productDto, new TypeToken<Product>() {
        }.getType());

        Product productAdded = productDao.save(product);

        if (productAdded == null) {
            return ResponseEntity.noContent().build();
        }

        //retour 201 si le produit est ajouté à la bdd 
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    //grâce à la regex de la classe SwaggerConfig.java, la doc ne contient
    //pas cette méthode
    @GetMapping(value = "/AdminProduits")
    public MappingJacksonValue calculerMargeProduit() {

        //on récupère l'ensemble des produits depuis la bdd
        List<Product> produits = productDao.findAll();
        //on mappe notre entité en dto
        //on ne renvoie jamais une entité au front-end !!!!!!!
        ModelMapper modelMapper = new ModelMapper();
        List<ProductDto> produitsDto = modelMapper.map(produits, new TypeToken<List<ProductDto>>() {
        }.getType());

        //le calcul de la marge etant trivial, il est mis directement
        //dans le getter du dto
        //on crée un filtre pour ne pas remonter le prix d'achat
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamiqueDto", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produitsDto);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }

    @DeleteMapping(value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {

        productDao.delete(id);
    }

    
    //grâce à la regex de la classe SwaggerConfig.java, la doc ne contient
    //pas cette méthode    
    @GetMapping(value = "/AdminProduitsTrierParNom")
    public MappingJacksonValue trierProduitsParOrdreAlphabetique() {

        //on récupère l'ensemble des produits ordonnés par leur nom
        //depuis la bdd
        List<Product> produits = productDao.findAllByOrderByNom();
        //on mappe notre entité en dto
        //on ne renvoie jamais une entité au front-end !!!!!!!
        ModelMapper modelMapper = new ModelMapper();
        List<ProductDto> produitsDto = modelMapper.map(produits, new TypeToken<List<ProductDto>>() {
        }.getType());

        //le calcul de la marge etant trivial, il est mis directement
        //dans le getter du dto
        //on crée un filtre pour ne pas remonter la marge
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("marge");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamiqueDto", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produitsDto);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }

    @PutMapping(value = "/Produits")
    public void updateProduit(@RequestBody Product product) {

        productDao.save(product);
    }

    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    public List<Product> testeDeRequetes(@PathVariable int prix) {

        return productDao.chercherUnProduitCher(400);
    }

}
