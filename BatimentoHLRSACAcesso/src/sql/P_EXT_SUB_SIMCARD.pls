CREATE OR REPLACE PROCEDURE p_ext_sub_simcard
IS
  /**
  * Procedure para processo de levantamento de informações de OS's de substituição de SIMCARD
  **/
  CURSOR ext_gsm
  IS
    SELECT * FROM tb_ext_s_simcard;
BEGIN
  EXECUTE IMMEDIATE 'truncate table tb_ext_s_simcard';
  COMMIT;
  BEGIN
    /**
    * Levantamento das OS's de Substituição de SIMCARD
    * Alterada Query de Insert para refletir o total de OS conforme solicitado pelo Duílio e o Sérgio
    **/
    /*INSERT INTO tb_ext_s_simcard
    (SELECT  COALESCE (c.entitle2contr_itm, c.x_entitle2x_contr_itm_h)
    AS objid_prod, id_number, c.objid AS v_objid_case,
    title, creation_time, NULL AS acesso, NULL AS imsi_hlr,
    NULL AS imsi_cly, NULL AS estado_hlr,
    NULL AS categoria_hlr, NULL AS tecnologia_hlr,
    NULL AS status_cly, NULL AS IMSI_GPP, NULL AS MSISDN_GPP,
    NULL AS IMSI_SISGEN
    FROM     table_case@TSIF3 c
    WHERE  c.creation_time > SYSDATE - 3
    AND x_ativ = 'Substituir SIM Card');*/
    INSERT
    INTO tb_ext_s_simcard
      (SELECT COALESCE (c.entitle2contr_itm, c.x_entitle2x_contr_itm_h) AS objid_prod,
          id_number,
          c.objid AS v_objid_case,
          title,
          creation_time,
          NULL AS acesso,
          NULL AS imsi_hlr,
          NULL AS imsi_cly,
          NULL AS estado_hlr,
          NULL AS categoria_hlr,
          NULL AS tecnologia_hlr,
          NULL AS status_cly,
          NULL AS IMSI_GPP,
          NULL AS MSISDN_GPP,
          NULL AS IMSI_SISGEN,
          NULL AS IMSI_CLY_HST,
          NULL AS SERVICO_HLR
        FROM table_case@dl_sif_crm c,
          table_contr_itm@dl_sif_crm p
        WHERE c.objid            > 1925041611
        AND c.x_data_fechamento >= TRUNC (sysdate-7)
        AND c.s_id_number LIKE '%OS%051'
        AND c.x_tipo_os = 'Substituir SIMCARD'
        AND p.objid    IN (c.entitle2contr_itm, c.x_entitle2x_contr_itm_h)
        AND p.status    = 'Ativo'
      );
    /*INSERT INTO tb_ext_s_simcard
    (SELECT  tci.objid AS objid_prod, NULL AS id_number, NULL AS v_objid_case,
    NULL title, NULL creation_time, NULL AS acesso, NULL AS imsi_hlr,
    NULL AS imsi_cly, NULL AS estado_hlr, NULL AS categoria_hlr,
    NULL AS tecnologia_hlr, NULL AS status_cly, NULL AS IMSI_GPP,
    NULL AS MSISDN_GPP, NULL AS IMSI_SISGEN, NULL AS IMSI_CLY_HST
    FROM  table_contr_itm@dl_prod tci, table_mod_level@dl_prod tml,
    table_part_num@dl_prod tpn
    WHERE       TCI.CONTR_ITM2MOD_LEVEL = TML.OBJID
    AND TML.PART_INFO2PART_NUM = TPN.OBJID
    AND tpn.part_number = 'PRD_ACESSO_MOVEL_ID0051'  );   */
  EXCEPTION
  WHEN OTHERS THEN
    NULL;
  END;
  /**
  * Iteração para atualizar com os Dados CRM
  **/
  FOR i IN ext_gsm
  LOOP
    BEGIN
      /**
      * Levantando o Acesso para a OS(Produção)
      **/
      UPDATE tb_ext_s_simcard
      SET acesso = TRIM (
        (SELECT quote_sn FROM table_contr_itm@dl_sif_crm WHERE objid = i.objid_prod
        ))
      WHERE objid_prod = i.objid_prod;
      /**
      * Levantamento do Status do Acesso
      **/
      UPDATE tb_ext_s_simcard
      SET status_cly = TRIM (
        (SELECT status FROM table_contr_itm@dl_sif_crm WHERE objid = i.objid_prod
        ))
      WHERE objid_prod = i.objid_prod;
      /**
      * Levantamento de IMSI(Produção)
      **/
      UPDATE tb_ext_s_simcard
      SET imsi_cly = TRIM (
        (SELECT x_valor_atrib
        FROM table_x_selec_atrib@dl_sif_crm
        WHERE x_selec_atrib2contr_itm IN
          (SELECT objid
          FROM table_contr_itm@dl_sif_crm
          WHERE child2contr_itm IN (i.objid_prod)
          )
        AND x_cod_atrib = 'simcard_imsi'
        ))
      WHERE objid_prod = i.objid_prod;
      /**
      * Levantamento de IMSI(Histórico)
      **/
      UPDATE tb_ext_s_simcard
      SET IMSI_CLY_HST = TRIM(
        (SELECT x_valor_antigo
        FROM table_x_selec_atrib_hst@dl_sif_crm
        WHERE x_indic_modificado     = 1
        AND x_sel_atr2x_contr_itm_h IN
          (SELECT objid
          FROM table_x_contr_itm_hst@dl_sif_crm
          WHERE x_child2x_contr_itm_h IN (i.objid_prod)
          )
        AND x_cod_atrib        = 'simcard_imsi'
        AND x_indic_historico IN
          (SELECT MAX ( x_indic_historico )
          FROM table_x_contr_itm_hst@dl_sif_crm
          WHERE objid            = i.objid_prod
          AND x_indic_modificado = 1
          )
        ))
      WHERE objid_prod = i.objid_prod;
      /**
      * Levantando o Acesso para a OS(Histórico)
      **/
      /* UPDATE  tb_ext_s_simcard
      SET acesso =
      TRIM( (SELECT    quote_sn
      FROM     table_x_contr_itm_hst@dl_prod
      WHERE  objid = i.objid_prod
      AND x_indic_historico IN
      (SELECT   MAX (
      x_indic_historico
      )
      FROM   table_x_contr_itm_hst@dl_prod
      WHERE   objid = i.objid_prod)))
      WHERE  objid_prod = i.objid_prod AND acesso IS NULL;*/
      /**
      * Levantamento de Status(Histórico)
      **/
      /*UPDATE  tb_ext_s_simcard
      SET status_cly =
      TRIM( (SELECT    STATUS
      FROM     table_x_contr_itm_hst@dl_prod
      WHERE  objid = i.objid_prod
      AND x_indic_historico IN
      (SELECT   MAX (
      x_indic_historico
      )
      FROM   table_x_contr_itm_hst@dl_prod
      WHERE   objid = i.objid_prod)))
      WHERE  objid_prod = i.objid_prod AND status_cly IS NULL;*/
      /* UPDATE  tb_ext_s_simcard
      SET IMSI_SISGEN =
      (SELECT  s.simc_imsi
      FROM     SISGEN.MSISDN_SIMCARD@dl_sisgen SM,
      SISGEN.SIMCARD@dl_sisgen S
      WHERE  S.simc_iccid = sm.msim_iccid
      AND SM.msim_msisdn IN (acesso))
      WHERE  objid_prod = i.objid_prod AND IMSI_SISGEN IS NULL;*/
    EXCEPTION
    WHEN OTHERS THEN
      NULL;
    END;
  END LOOP;
  COMMIT;
END; 