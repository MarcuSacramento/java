create or replace PROCEDURE p_batimento_movel
IS
     v_flag_imsi_hlr            NUMBER;
     v_flag_imsi_cly            NUMBER;
     v_flag_imsi_gpp            NUMBER;
     v_flag_imsi_sisgen     NUMBER;
     v_flag_erro                    NUMBER;
     v_divergencia              VARCHAR (80);
     v_bloqueio                     VARCHAR2 (4000);
     v_servico                      VARCHAR2 (4000);

     CURSOR tabela_batimento
     IS
            SELECT   *
                FROM     tb_batimento_movel
             WHERE   TRUNC (data_batimento) = TRUNC (SYSDATE);
BEGIN
     INSERT INTO tb_batimento_movel
            (SELECT     EXT.OBJID_PROD, EXT.ID_NUMBER, EXT.V_OBJID_CASE, EXT.TITLE, EXT.CREATION_TIME, EXT.ACESSO, EXT.IMSI_HLR, EXT.IMSI_CLY, EXT.ESTADO_HLR, EXT.CATEGORIA_HLR,
                                EXT.TECNOLOGIA_HLR, EXT.STATUS_CLY, EXT.IMSI_GPP, EXT.MSISDN_GPP, EXT.IMSI_SISGEN, EXT.IMSI_CLY_HST,
                                (SELECT     data
                                     FROM   x_vitria_in@dl_sif_crm
                                    WHERE           row_id = ext.v_objid_case
                                                    AND business_event = 'Fechar OS SMP'
                                                    AND data IN (SELECT     MIN (data)
                                                                                 FROM   x_vitria_in@dl_sif_crm
                                                                                WHERE   row_id = ext.v_objid_case AND business_event = 'Fechar OS SMP'))
                                     AS FECHAMENTO_OS, SYSDATE AS DATA_BATIMENTO, NULL AS STATUS_BATIMENTO, NULL AS BLOQUEIOS, NULL AS SERVICOS
                 FROM   tb_ext_s_simcard ext);

     /*WHERE ( ext.imsi_hlr != ext.imsi_cly
    OR ext.imsi_hlr != ext.imsi_gpp
    OR ext.imsi_gpp != ext.imsi_cly)
     AND ext.status_cly != 'Inativo');*/
     COMMIT;

     FOR i IN tabela_batimento
     LOOP
            v_flag_imsi_hlr := 0;
            v_flag_imsi_cly := 0;
            v_flag_imsi_gpp := 0;
            v_flag_imsi_sisgen := 0;
            v_divergencia := '';
            v_bloqueio := '';
            v_servico := '';

            /**
             *Calculando Divergência CLY
             **/
            IF (i.imsi_cly != i.imsi_hlr)
            THEN
                 v_flag_imsi_cly := v_flag_imsi_cly + 1;
            END IF;

            IF (i.imsi_cly != i.imsi_gpp)
            THEN
                 v_flag_imsi_cly := v_flag_imsi_cly + 1;
            END IF;

            IF (i.imsi_cly != i.imsi_sisgen)
            THEN
                 v_flag_imsi_cly := v_flag_imsi_cly + 1;
            END IF;

            /**
             *Calculando Divergência GPP
             **/
            IF (i.imsi_gpp != i.imsi_cly)
            THEN
                 v_flag_imsi_gpp := v_flag_imsi_gpp + 1;
            END IF;

            IF (i.imsi_gpp != i.imsi_hlr)
            THEN
                 v_flag_imsi_gpp := v_flag_imsi_gpp + 1;
            END IF;

            IF (i.imsi_gpp != i.imsi_sisgen)
            THEN
                 v_flag_imsi_gpp := v_flag_imsi_gpp + 1;
            END IF;

            /**
             *Calculando Divergência HLR
             **/
            IF (i.imsi_hlr != i.imsi_cly)
            THEN
                 v_flag_imsi_hlr := v_flag_imsi_hlr + 1;
            END IF;

            IF (i.imsi_hlr != i.imsi_gpp)
            THEN
                 v_flag_imsi_hlr := v_flag_imsi_hlr + 1;
            END IF;

            IF (i.imsi_hlr != i.imsi_sisgen)
            THEN
                 v_flag_imsi_hlr := v_flag_imsi_hlr + 1;
            END IF;

            /**
             *Calculando Divergência SISGEN
             **/
            IF (i.imsi_sisgen != i.imsi_cly)
            THEN
                 v_flag_imsi_sisgen := v_flag_imsi_sisgen + 1;
            END IF;

            IF (i.imsi_sisgen != i.imsi_gpp)
            THEN
                 v_flag_imsi_sisgen := v_flag_imsi_sisgen + 1;
            END IF;

            IF (i.imsi_sisgen != i.imsi_hlr)
            THEN
                 v_flag_imsi_sisgen := v_flag_imsi_sisgen + 1;
            END IF;



            v_flag_erro := 0;

            /**
             *Checando divergência GPP
            **/

            IF ( (v_flag_imsi_gpp > v_flag_imsi_cly AND v_flag_imsi_gpp > v_flag_imsi_hlr AND v_flag_imsi_gpp > v_flag_imsi_sisgen))
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Diverge no GPP';
                 v_flag_erro := v_flag_erro + 1;
            END IF;



            IF (i.imsi_gpp IS NULL AND (TRIM (i.categoria_hlr) = 'pre_pago' OR TRIM (i.categoria_hlr) = 'hibrido'))
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Nulo no GPP';
                 v_flag_erro := v_flag_erro + 1;
            END IF;



            /**
             *Checando divergência CLY
            **/

            IF ( (v_flag_imsi_cly > v_flag_imsi_gpp AND v_flag_imsi_cly > v_flag_imsi_hlr AND v_flag_imsi_cly > v_flag_imsi_sisgen))
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Diverge no CLY';
                 v_flag_erro := v_flag_erro + 1;
            END IF;



            IF (i.imsi_cly IS NULL)
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Nulo no CLY';
                 v_flag_erro := v_flag_erro + 1;
            END IF;



            /**
             *Checando divergência HLR
            **/
            IF ( (v_flag_imsi_hlr > v_flag_imsi_gpp AND v_flag_imsi_hlr > v_flag_imsi_cly AND v_flag_imsi_hlr > v_flag_imsi_sisgen))
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Diverge no HLR';
                 v_flag_erro := v_flag_erro + 1;
            END IF;



            IF (i.imsi_hlr IS NULL)
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Nulo no HLR';
                 v_flag_erro := v_flag_erro + 1;
            END IF;



            /**
             *Checando divergência SISGEN
            **/

            IF ( (v_flag_imsi_sisgen > v_flag_imsi_gpp AND v_flag_imsi_sisgen > v_flag_imsi_cly AND v_flag_imsi_sisgen > v_flag_imsi_hlr))
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Diverge no SISGEN';
                 v_flag_erro := v_flag_erro + 1;
            END IF;



            IF (i.imsi_sisgen IS NULL)
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Nulo no SISGEN';
                 v_flag_erro := v_flag_erro + 1;
            END IF;

            IF ( (v_flag_imsi_gpp > 0 AND v_flag_imsi_cly > 0 AND v_flag_imsi_gpp > 0 AND v_flag_imsi_hlr > 0 AND v_flag_imsi_sisgen > 0)
                    AND (v_divergencia IS NULL OR v_divergencia = ''))
            THEN
                 IF (v_flag_erro > 0)
                 THEN
                        v_divergencia := v_divergencia || ';';
                        v_flag_erro := 0;
                 END IF;

                 v_divergencia := v_divergencia || 'Diverge em + de 1 Sistema';
                 v_flag_erro := v_flag_erro + 1;
            END IF;

            IF (v_divergencia = '' OR v_divergencia IS NULL)
            THEN
                 v_divergencia := 'Verificar';

                 IF ( (v_flag_imsi_gpp = 0 AND v_flag_imsi_cly = 0 AND v_flag_imsi_hlr = 0 AND v_flag_imsi_sisgen = 0))
                 THEN
                        v_divergencia := 'Sem Divergência';
                 END IF;
            END IF;

            UPDATE   tb_batimento_movel
                 SET     status_batimento = v_divergencia
             WHERE   objid_prod = i.objid_prod;

            SELECT  '"'|| decode(REGEXP_SUBSTR (bloqueio, '[L][D][I]') ,'','N','LDI','Y','E')
    || '";"' || decode(REGEXP_SUBSTR (bloqueio, '[L][D][N]') ,'','N','LDN','Y','E')
    || '";"' || decode(REGEXP_SUBSTR (bloqueio, '[T][o][t][a][l]') ,'','N','Total','Y','E')
    || '";"' || decode(REGEXP_SUBSTR (bloqueio, '[R][o][a][m][i][n][g][ ][t][o][t][a][l]') ,'','N','Roaming total','Y','E')
    || '";"' || decode(REGEXP_SUBSTR (bloqueio, '[R][o][a][m][i][n][g][ ][i][n][t][e][r][n][a][c][i][o][n][a][l]') ,'','N','Roaming internacional','Y','E')
    || '";"' || decode(REGEXP_SUBSTR (bloqueio, '[R][e][c][e][b][i][m][e][n][t][o][ ][a][ ][c][o][b][r][a][r]'),'','N','Recebimento a cobrar','Y','E'),
     '";"'||    decode(REGEXP_SUBSTR (servico, '[C][a][i][x][a][ ][P][o][s][t][a][l]'),'','N','Caixa Postal','Y','E')
                             || '";"'
                             || decode(REGEXP_SUBSTR (servico, '[C][a][i][x][a][ ][P][o][s][t][a][l][ ][c][o][m][ ][p][r][o][b][l][e][m][a][s]'),'','N','Caixa Postal com problemas','Y','E')
                             || '";"'
                             || decode(REGEXP_SUBSTR (servico, '[C][h][a][m][a][d][a][ ][e][m][ ][e][s][p][e][r][a]'),'','N','Chamada em espera','Y','E')
                             || '";"'
                             || decode(REGEXP_SUBSTR (servico, '[S][i][g][a][-][m][e]'),'','N','Siga-me','Y','E')
                             || '";"'
                             || decode(REGEXP_SUBSTR (servico, '[S][M][S]'),'','N','SMS','Y','E')
                             || '";"'
                             || decode(REGEXP_SUBSTR (servico, '[C][o][n][f][e][r][e][n][c][i][a]'),'','N','Conferencia','Y','E')
                             || '";"'
                             || decode(REGEXP_SUBSTR (servico, '[I][d][e][n][t][i][f][i][c][a][d][o][r][ ][d][e][ ][c][h][a][m][a][d][a][s]'),'','N','Identificador de chamadas','Y','E')
                             || '";"'
                             || decode(REGEXP_SUBSTR (servico, '[S][M][S][ ][s][o][''''][ ][r][e][c][e][b][i][m][e][n][t][o]'),'','N','SMS so'' recebimento','Y','E')
                             || '";"'
                             || decode(LTRIM(SUBSTR( REGEXP_SUBSTR (servico, '[G][P][R][S][:][ ][A-Z0-9]{1,2}'),-2)),'Y','Y','N','N','3G','3G','','E')
                             || '";"'
                             || decode(LTRIM(SUBSTR( REGEXP_SUBSTR (servico, '[M][M][S][:][ ][A-Z0-9]{1,2}'),-2)),'Y','Y','N','N','3G','3G','','E')
                             || '";"'
                             || decode(LTRIM(SUBSTR( REGEXP_SUBSTR (servico, '[W][A][P][:][ ][A-Z0-9]{1,2}'),-2)),'Y','Y','N','N','3G','3G','','E')
                INTO     v_bloqueio, v_servico
                FROM     (SELECT     REGEXP_SUBSTR (SERVICO_HLR, '[B][l][o][q][u][e][i][o][s][:].*') || REGEXP_SUBSTR (SERVICO_HLR, '[B][l][o][q][u][e][i][o][s][:].*', 1, 2) AS Bloqueio,
                                                        REGEXP_SUBSTR (SERVICO_HLR, '[F][e][a][t][u][r][e][s][:].*')
                                                 || REGEXP_SUBSTR (SERVICO_HLR, '[F][e][a][t][u][r][e][s][:].*', 1, 2)
                                                 || REGEXP_SUBSTR (SERVICO_HLR, '[G][P][R][S][:].*')
                                                        AS SERVICO
                                    FROM     tb_ext_s_simcard a
                                 WHERE   a.objid_prod = i.objid_prod);

    UPDATE   tb_batimento_movel
                 SET bloqueios = v_bloqueio,
                 servicos = v_servico
             WHERE   objid_prod = i.objid_prod;



            COMMIT;
     END LOOP;

     COMMIT;
END; 
 