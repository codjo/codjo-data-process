/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import org.junit.Test;
/**
 *
 */
public class XMLUtilsTest {
    @Test
    public void flatten() {
        String strXml =
              "<treatment id=\"transcoVl\" scope=\"TREATMENT\" type=\"sql\">       <comment/>       <title>Transco AP_VL_GPFO &gt; TI_CSCOP_VL_10"
              + "</title><target>delete         TI_CSCOP_VL_10 where PERIODE = ? and SYSTEME= ?\n\ninsert into TI_CSCOP_VL_10 (PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, INSTRUMENT, PLACE_COTATION, STATUT, STATUT_LIGNE, SYSTEME ) \nselect PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, ?, ?, ?, ?, ? from AP_VL_GPFO where AP_VL_GPFO.PERIODE = ?</target>\n"
              + "<result-table/>\n<arguments><arg position=\"1\" type=\"12\"><name>periodeDelete</name><value>$periode$</value></arg><arg position=\"2\" type=\"12\"><name>systeme.1</name><value>GPFO</value></arg><arg position=\"3\" type=\"12\"><name>instrument</name><value>SICAV</value></arg><arg position=\"4\" type=\"12\"><name>placeCotation</name><value>XXX</value></arg><arg position=\"5\" type=\"12\"><name>statut</name><value>OUVERT</value></arg><arg position=\"6\" type=\"12\"><name>statutLigne</name><value>PROPRE</value></arg><arg position=\"7\" type=\"12\"><name>systeme.2</name><value>GPFO</value></arg><arg position=\"8\" type=\"12\"><name>periodeWhere</name><value>$periode$</value></arg></arguments></treatment>";

        String result = XMLUtils.flatten(strXml, true);
        assertThat(result,
                   equalTo(
                         "<treatment id=\"transcoVl\" scope=\"TREATMENT\" type=\"sql\"> <comment/> <title>Transco AP_VL_GPFO &gt; TI_CSCOP_VL_10</title><target>delete TI_CSCOP_VL_10 where PERIODE = ? and SYSTEME= ?insert into TI_CSCOP_VL_10 (PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, INSTRUMENT, PLACE_COTATION, STATUT, STATUT_LIGNE, SYSTEME ) select PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, ?, ?, ?, ?, ? from AP_VL_GPFO where AP_VL_GPFO.PERIODE = ?</target><result-table/><arguments><arg position=\"1\" type=\"12\"><name>periodeDelete</name><value>$periode$</value></arg><arg position=\"2\" type=\"12\"><name>systeme.1</name><value>GPFO</value></arg><arg position=\"3\" type=\"12\"><name>instrument</name><value>SICAV</value></arg><arg position=\"4\" type=\"12\"><name>placeCotation</name><value>XXX</value></arg><arg position=\"5\" type=\"12\"><name>statut</name><value>OUVERT</value></arg><arg position=\"6\" type=\"12\"><name>statutLigne</name><value>PROPRE</value></arg><arg position=\"7\" type=\"12\"><name>systeme.2</name><value>GPFO</value></arg><arg position=\"8\" type=\"12\"><name>periodeWhere</name><value>$periode$</value></arg></arguments></treatment>"));

        result = XMLUtils.flatten(strXml, false);
        assertThat(result,
                   equalTo(
                         "<treatment id=\"transcoVl\" scope=\"TREATMENT\" type=\"sql\">       <comment/>       <title>Transco AP_VL_GPFO &gt; TI_CSCOP_VL_10</title><target>delete         TI_CSCOP_VL_10 where PERIODE = ? and SYSTEME= ?insert into TI_CSCOP_VL_10 (PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, INSTRUMENT, PLACE_COTATION, STATUT, STATUT_LIGNE, SYSTEME ) select PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, ?, ?, ?, ?, ? from AP_VL_GPFO where AP_VL_GPFO.PERIODE = ?</target><result-table/><arguments><arg position=\"1\" type=\"12\"><name>periodeDelete</name><value>$periode$</value></arg><arg position=\"2\" type=\"12\"><name>systeme.1</name><value>GPFO</value></arg><arg position=\"3\" type=\"12\"><name>instrument</name><value>SICAV</value></arg><arg position=\"4\" type=\"12\"><name>placeCotation</name><value>XXX</value></arg><arg position=\"5\" type=\"12\"><name>statut</name><value>OUVERT</value></arg><arg position=\"6\" type=\"12\"><name>statutLigne</name><value>PROPRE</value></arg><arg position=\"7\" type=\"12\"><name>systeme.2</name><value>GPFO</value></arg><arg position=\"8\" type=\"12\"><name>periodeWhere</name><value>$periode$</value></arg></arguments></treatment>"));
    }


    @Test
    public void flattenAndReplaceSpecialChar() {
        String strXml =
              "<treatment id=\"transcoVl\"       scope=\"TREATMENT\"       type=\"sql\"><comment/><title>Transco AP_VL_GPFO &gt; TI_CSCOP_VL_10"
              + "</title><target>delete       TI_CSCOP_VL_10 where PERIODE = ? and SYSTEME= ?\n\ninsert into TI_CSCOP_VL_10 (PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, INSTRUMENT, PLACE_COTATION, STATUT, STATUT_LIGNE, SYSTEME ) \nselect PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, ?, ?, ?, ?, ? from AP_VL_GPFO where AP_VL_GPFO.PERIODE = ?</target>\n"
              + "<result-table/>\n<arguments><arg position=\"1\" type=\"12\"><name>periodeDelete</name><value>$periode$</value></arg><arg position=\"2\" type=\"12\"><name>systeme.1</name><value>GPFO</value></arg><arg position=\"3\" type=\"12\"><name>instrument</name><value>SICAV</value></arg><arg position=\"4\" type=\"12\"><name>placeCotation</name><value>XXX</value></arg><arg position=\"5\" type=\"12\"><name>statut</name><value>OUVERT</value></arg><arg position=\"6\" type=\"12\"><name>statutLigne</name><value>PROPRE</value></arg><arg position=\"7\" type=\"12\"><name>systeme.2</name><value>GPFO</value></arg><arg position=\"8\" type=\"12\"><name>periodeWhere</name><value>$periode$</value></arg></arguments></treatment>";

        String result = XMLUtils.flattenAndReplaceCRLF(strXml, true);
        assertThat(result,
                   equalTo(
                         "<treatment id=\"transcoVl\" scope=\"TREATMENT\" type=\"sql\"><comment/><title>Transco AP_VL_GPFO &gt; TI_CSCOP_VL_10"
                         + "</title><target>delete TI_CSCOP_VL_10 where PERIODE = ? and SYSTEME= ?[@][@]insert into TI_CSCOP_VL_10 (PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, INSTRUMENT, PLACE_COTATION, STATUT, STATUT_LIGNE, SYSTEME ) [@]select PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, ?, ?, ?, ?, ? from AP_VL_GPFO where AP_VL_GPFO.PERIODE = ?</target>[@]"
                         + "<result-table/>[@]<arguments><arg position=\"1\" type=\"12\"><name>periodeDelete</name><value>$periode$</value></arg><arg position=\"2\" type=\"12\"><name>systeme.1</name><value>GPFO</value></arg><arg position=\"3\" type=\"12\"><name>instrument</name><value>SICAV</value></arg><arg position=\"4\" type=\"12\"><name>placeCotation</name><value>XXX</value></arg><arg position=\"5\" type=\"12\"><name>statut</name><value>OUVERT</value></arg><arg position=\"6\" type=\"12\"><name>statutLigne</name><value>PROPRE</value></arg><arg position=\"7\" type=\"12\"><name>systeme.2</name><value>GPFO</value></arg><arg position=\"8\" type=\"12\"><name>periodeWhere</name><value>$periode$</value></arg></arguments></treatment>"));

        result = XMLUtils.flattenAndReplaceCRLF(strXml, false);
        assertThat(result,
                   equalTo(
                         "<treatment id=\"transcoVl\"       scope=\"TREATMENT\"       type=\"sql\"><comment/><title>Transco AP_VL_GPFO &gt; TI_CSCOP_VL_10"
                         + "</title><target>delete       TI_CSCOP_VL_10 where PERIODE = ? and SYSTEME= ?[@][@]insert into TI_CSCOP_VL_10 (PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, INSTRUMENT, PLACE_COTATION, STATUT, STATUT_LIGNE, SYSTEME ) [@]select PERIODE, CODE_PORTEFEUILLE, LIBELLE_PORTEFEUILLE, CODE_VALEUR, CODE_COMMISSAIRE, DEVISE, NOMBRE_PARTS, VALEUR_LIQUIDATIVE_UNITAIRE, DATE_VALORISATION, DATE_TRAVAIL, CODE_ISIN, ?, ?, ?, ?, ? from AP_VL_GPFO where AP_VL_GPFO.PERIODE = ?</target>[@]"
                         + "<result-table/>[@]<arguments><arg position=\"1\" type=\"12\"><name>periodeDelete</name><value>$periode$</value></arg><arg position=\"2\" type=\"12\"><name>systeme.1</name><value>GPFO</value></arg><arg position=\"3\" type=\"12\"><name>instrument</name><value>SICAV</value></arg><arg position=\"4\" type=\"12\"><name>placeCotation</name><value>XXX</value></arg><arg position=\"5\" type=\"12\"><name>statut</name><value>OUVERT</value></arg><arg position=\"6\" type=\"12\"><name>statutLigne</name><value>PROPRE</value></arg><arg position=\"7\" type=\"12\"><name>systeme.2</name><value>GPFO</value></arg><arg position=\"8\" type=\"12\"><name>periodeWhere</name><value>$periode$</value></arg></arguments></treatment>"));
    }


    @Test
    public void getRidOfHeader() {
        String str =
              "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n\naaaaaaaaa\r\r\n"
              + "<treatment id=\"CexportCapitalDiminution\" scope=\"TREATMENT\" type=\"sql_with_result\">";
        assertThat(
              "<treatment id=\"CexportCapitalDiminution\" scope=\"TREATMENT\" type=\"sql_with_result\">",
              equalTo(XMLUtils.getRidOfHeader(str)));
    }
}
