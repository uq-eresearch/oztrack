package org.oztrack.data.access.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.OaiPmhEntityProducer;
import org.oztrack.data.access.OaiPmhSetDao;
import org.oztrack.data.model.Country;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.types.OaiPmhSet;
import org.springframework.stereotype.Service;

@Service
public class OaiPmhSetDaoImpl implements OaiPmhSetDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public OaiPmhEntityProducer<OaiPmhSet> getSets() {
        OaiPmhEntityProducer<OaiPmhSet> institutionSetProducer = new OaiPmhEntityProducer<OaiPmhSet>() {
            @Override
            public Iterator<OaiPmhSet> iterator() {
                return Arrays.asList(new OaiPmhSet("institution", "Institution")).iterator();
            }
        };

        @SuppressWarnings("unchecked")
        final List<Institution> institutions = em.createQuery("from org.oztrack.data.model.Institution order by title").getResultList();
        OaiPmhEntityProducer<OaiPmhSet> institutionSubsetProducer =
            new OaiPmhMappingEntityProducer<Institution, OaiPmhSet>(institutions.iterator()) {
                @Override
                protected OaiPmhSet map(Institution institution) {
                    return new OaiPmhSet("institution:" + institution.getId(), institution.getTitle());
                }
            };

        OaiPmhEntityProducer<OaiPmhSet> countrySetProducer = new OaiPmhEntityProducer<OaiPmhSet>() {
            @Override
            public Iterator<OaiPmhSet> iterator() {
                return Arrays.asList(new OaiPmhSet("country", "Country")).iterator();
            }
        };

        @SuppressWarnings("unchecked")
        final List<Country> countries = em.createQuery("from org.oztrack.data.model.Country order by title").getResultList();
        OaiPmhEntityProducer<OaiPmhSet> countrySubsetProducer =
            new OaiPmhMappingEntityProducer<Country, OaiPmhSet>(countries.iterator()) {
                @Override
                protected OaiPmhSet map(Country country) {
                    return new OaiPmhSet("country:" + country.getCode().toLowerCase(Locale.ENGLISH), country.getTitle());
                }
            };

        @SuppressWarnings("unchecked")
        List<OaiPmhEntityProducer<OaiPmhSet>> producers = Arrays.asList(
            institutionSetProducer,
            institutionSubsetProducer,
            countrySetProducer,
            countrySubsetProducer
        );
        return new OaiPmhChainingEntityProducer<OaiPmhSet>(producers);
    }
}
