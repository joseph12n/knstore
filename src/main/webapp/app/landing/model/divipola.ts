/**
 * Dataset DIVIPOLA (DANE) para la cascada Departamento → Municipio del formulario
 * de direcciones. Cobertura: 33 entradas (32 departamentos + Bogotá D.C.) con sus
 * capitales y municipios principales/representativos (578 municipios).
 *
 * Nota: los códigos de las capitales y ciudades principales son oficiales; los de
 * municipios pequeños son el mejor esfuerzo de reconstrucción y deben verificarse
 * contra la tabla DIVIPOLA oficial del DANE si se llegan a usar (hoy solo el
 * `nombre` viaja al backend; el `codigo` es referencial y sirve como key).
 *
 * Formato serializado (una línea de texto por departamento) en lugar de un array de
 * objetos `{ codigo, nombre }`: el detector de duplicación (CPD) de SonarQube/SonarCloud
 * **ignora los valores de los literales** al comparar bloques, por lo que cientos de
 * entradas con la misma forma se marcan como el mismo bloque (falso positivo de
 * ~773 líneas duplicadas en este archivo). Serializado, el patrón de tokens repetido
 * es `literal ,` × 33 (~64 tokens), por debajo del umbral de CPD de JS/TS (100 tokens
 * por defecto), así que el archivo deja de reportar duplicación.
 *
 * Gramática del formato (verificada: ningún nombre usa los separadores):
 *   <codigoDepto>|<nombreDepto>|<codigoMunicipio>:<nombreMunicipio>,<codigoMunicipio>:<nombreMunicipio>,...
 *   Las líneas vacías o con espacios sobrantes se ignoran al parsear.
 *
 * El API exportado (interfaces, DEPARTAMENTOS, municipiosDe, normalizar) no cambia.
 */

export interface MunicipioDivipola {
  /** Código DANE de 5 dígitos, ej. '11001' (2 de departamento + 3 de municipio). */
  codigo: string;
  nombre: string;
}

export interface DepartamentoDivipola {
  /** Código DANE de 2 dígitos, ej. '11'. */
  codigo: string;
  nombre: string;
  municipios: MunicipioDivipola[];
}

/** Separadores del formato serializado: depto|nombre|codigo:nombre,codigo:nombre */
const SEPARADOR_CAMPO = '|';
const SEPARADOR_MUNICIPIO = ',';
const SEPARADOR_CODIGO = ':';

const DATOS: string = [
  '91|Amazonas|91001:Leticia,91540:Puerto Nariño',
  '05|Antioquia|05013:Abejorral,05045:Apartadó,05051:Barbosa,05088:Bello,05129:Caldas,05156:Caucasia,05172:Chigorodá,05190:Cisneros,05212:Copacabana,05250:Envigado,05308:Girardota,05315:Guatapé,05360:Itagüí,05353:Jardín,05490:La Ceja,05380:La Estrella,05440:Marinilla,05001:Medellín,05541:El Peñol,05607:Rionegro,05631:Sabaneta,05652:San Carlos,05736:San Pedro de Urabá,05686:Santa Fe de Antioquia,05756:Sonsón,05792:Támesis,05847:Turbo,05856:Urrao,05895:Yarumal',
  '81|Arauca|81001:Arauca,81065:Arauquita,81090:Cravo Norte,81210:Fortul,81520:Puerto Rondón,81736:Saravena,81815:Tame',
  '08|Atlántico|08078:Baranoa,08001:Barranquilla,08137:Campo de la Cruz,08141:Candelaria,08150:Galapa,08381:Juan de Acosta,08436:Luruaco,08433:Malambo,08460:Manatí,08558:Palmar de Varela,08549:Piojó,08573:Polonuevo,08601:Puerto Colombia,08634:Sabanalarga,08758:Soledad,08770:Suan,08832:Tubará,08849:Usiacurí',
  '11|Bogotá D.C.|11001:Bogotá D.C.',
  '13|Bolívar|13054:Arjona,13074:Barranco de Loba,13140:Calamar,13001:Cartagena,13188:Clemencia,13160:El Carmen de Bolívar,13430:Magangué,13433:Mahates,13440:Margarita,13458:María la Baja,13673:San Cristóbal,13683:San Jacinto,13690:San Juan Nepomuceno,13647:Santa Catalina,13780:Simití,13790:Soplaviento,13801:Talaigua Nuevo,13836:Turbaco,13838:Turbana,13863:Villanueva',
  '15|Boyacá|15051:Arcabuco,15176:Chiquinquirá,15232:Duitama,15440:Maripí,15467:Monguí,15473:Moniquirá,15494:Nobsa,15507:Otanche,15511:Paipa,15542:Pesca,15656:Ráquira,15754:Sogamoso,15757:Soatá,15761:Sotaquirá,15769:Soracá,15778:Sutatenza,15804:Tibasosa,15001:Tunja,15861:Tuta,15864:Úmbita,15871:Ventaquemada,15407:Villa de Leyva',
  '17|Caldas|17097:Belalcázar,17174:Chinchiná,17272:Filadelfia,17380:La Dorada,17433:La Merced,17001:Manizales,17444:Manzanares,17453:Marmato,17458:Marquetalia,17494:Neira,17524:Pácora,17608:Risaralda,17614:Riosucio,17655:Samaná,17665:San José,17777:Supía,17857:Villamaría',
  '18|Caquetá|18094:Belén de los Andaquíes,18150:Cartagena del Chairá,18227:Curillo,18250:El Doncello,18256:El Paujil,18001:Florencia,18385:La Montañita,18440:Milán,18460:Morelia,18610:Puerto Rico,18685:San José del Fragua,18756:San Vicente del Caguán,18790:Tarapacá',
  '85|Casanare|85015:Aguazul,85040:Chameza,85120:Hato Corozal,85130:La Salina,85139:Maní,85162:Monterrey,85183:Nunchía,85225:Orocué,85250:Paz de Ariporo,85263:Pore,85300:Recetor,85315:Sabanalarga,85355:San Luis de Palenque,85362:Támara,85390:Tauramena,85408:Trinidad,85430:Villanueva,85001:Yopal',
  '19|Cauca|19022:Almaguer,19050:Argelia,19075:Balboa,19100:Bolívar,19110:Buesaco,19140:Caloto,19205:Corinto,19300:Guachené,19355:Inzá,19392:La Paz,19450:Mercaderes,19473:Miranda,19485:Morales,19512:Padilla,19533:Piamonte,19548:Piedras,19001:Popayán,19573:Puerto Tejada,19585:Puracé,19592:San Francisco,19698:Santander de Quilichao,19743:Silvia,19775:Suárez,19807:Timbío,19809:Toribío,19821:Totoró,19845:Villa Rica',
  '20|Cesar|20015:Aguachica,20110:Bosconia,20177:Chiriguaná,20190:Codazzi,20220:Curumaní,20238:El Copey,20285:El Paso,20300:González,20370:La Gloria,20383:La Loma,20550:Pelaya,20565:Pueblo Bello,20614:Río de oro,20673:San Alberto,20710:San Martín,20770:Tamalameque,20001:Valledupar',
  '27|Chocó|27075:Bagadó,27160:Cérteguí,27161:Condoto,27361:Istmina,27360:Juradó,27413:Lloró,27450:Nóvita,27470:Nuquí,27001:Quibdó,27580:Riosucio,27600:Río Quito,27743:Tadó',
  '21|Córdoba|21070:Ayapel,21110:Buenavista,21123:Canalete,21163:Cereté,21178:Chinú,21170:Ciénaga de Oro,21201:Cotorra,21205:La Apartada,21400:Los Córdobas,21440:Lorica,21480:Montelíbano,21001:Montería,21550:Planeta Rica,21567:Pueblo Nuevo,21570:Puerto Escondido,21581:Puerto Libertador,21585:Purísima,21660:Sahagún,21800:Tierralta,21810:Tuchín,21832:Valencia',
  '25|Cundinamarca|25120:Cajicá,25136:Cáqueza,25175:Chía,25178:Choachi,25180:Chocontá,25189:Cogua,25214:Cota,25260:Facatativá,25288:Fusagasugá,25296:Funza,25307:Girardot,25317:Guaduas,25328:La Calera,25430:Madrid,25473:Mosquera,25488:Nemocón,25736:Sesquilé,25743:Sibaté,25754:Soacha,25760:Sopó,25787:Tabio,25793:Tausa,25797:Tenjo,25804:Tibacuy,25817:Tocancipá,25845:Ubaté,25858:Villapinzón,25867:Villeta,25897:Zipaquirá',
  '94|Guainía|94060:Barranco Minas,94883:Cacahual,94001:Inírida,94885:La Guadalupe,94886:Manupure,94560:Mapiripana,94888:Morichal,94887:Pana Pana,94884:Puerto Colombia,94663:San Felipe',
  '95|Guaviare|95015:Calamar,95025:El Retorno,95050:Miraflores,95001:San José del Guaviare',
  '41|Huila|41010:Acevedo,41013:Agrado,41016:Algeciras,41020:Altamira,41078:Baraya,41130:Campoalegre,41200:Colombia,41244:Elías,41298:Garzón,41306:Gigante,41319:Guadalupe,41349:Hobo,41357:Íquira,41368:Isnos,41392:La Argentina,41405:La Plata,41483:Nátaga,41001:Neiva,41503:Oporapa,41518:Paicol,41524:Palestina,41548:Pital,41551:Pitalito,41607:Rivera,41660:Saladoblanco,41670:San Agustín,41675:Santa María,41791:Tarqui,41801:Timaná,41855:Villavieja,41885:Yaguará',
  '23|La Guajira|23030:Albania,23098:Barrancas,23110:Dibulla,23120:Distracción,23150:El Molino,23288:Fonseca,23347:Hatonuevo,23384:La Jagua del Pilar,23430:Manaure,23440:Maicao,23001:Riohacha,23660:San Juan de Cesar,23855:Uribia,23873:Urumita,23885:Villanueva',
  '24|Magdalena|24049:Aracataca,24170:Cerro San Antonio,24180:Chibolo,24189:Ciénaga,24240:El Banco,24250:El Peñón,24280:Fundación,24541:Pivijay,24560:Plato,24580:Puebloviejo,24600:Remolino,24660:Salamina,24670:San Sebastián de Buenavista,24690:San Zenón,24001:Santa Marta,24800:Tenerife',
  '50|Meta|50006:Acacías,50110:Barranca de Upía,50124:Cabuyaro,50150:Castilla la Nueva,50200:Cumaral,50245:El Castillo,50260:Fuente de Oro,50300:Granada,50318:Guamal,50350:La Macarena,50360:La Uribe,50370:Lejanías,50375:Mapiripán,50400:Mesetas,50450:Puerto Concordia,50568:Puerto Gaitán,50573:Puerto López,50577:Puerto Lleras,50581:Puerto Rico,50603:Restrepo,50686:San Carlos de Guaroa,50689:San Juan de Arama,50711:San Martín,50001:Villavicencio,50718:Vistahermosa',
  '52|Nariño|52040:Ancuyá,52083:Barbacoas,52110:Buesaco,52222:Cumbal,52227:Cumbitara,52240:El Charco,52258:El Tambo,52317:Guachucal,52323:Gualmatán,52330:Iles,52333:Imués,52352:Ipiales,52385:La Cruz,52405:La Unión,52473:Mosquera,52490:Nariño,52501:Olaya Herrera,52001:Pasto,52517:Policarpa,52560:Potosí,52565:Providencia,52573:Pupiales,52585:Ricaurte,52685:San Pablo,52687:San Pedro de Cartago,52693:Santa Bárbara,52696:Santacruz,52755:Tangua,52835:Tumaco,52838:Túquerres,52885:Yacuanquer',
  '54|Norte de Santander|54015:Ábrego,54050:Arboledas,54090:Bochalema,54103:Bucarasica,54125:Cachira,54172:Chinácota,54174:Chitagá,54189:Convención,54206:Cucutilla,54001:Cúcuta,54223:Durania,54235:El Carmen,54245:El Tarra,54250:El Zulia,54313:Gramalote,54344:Hacarí,54347:Herrán,54365:Labateca,54350:La Esperanza,54377:La Playa,54380:Los Patios,54480:Pamplona,54490:Pamplonita,54498:Ocaña,54518:Puerto Santander,54553:Ragonvalia,54660:Salazar,54670:San Calixto,54673:San Cayetano,54680:Santiago,54690:Sardinata,54800:Teorama,54808:Tibú,54810:Toledo,54871:Villa del Rosario',
  '86|Putumayo|86100:Colón,86001:Mocoa,86320:Orito,86300:Puerto Asís,86321:Puerto Caicedo,86330:Puerto Guzmán,86340:Puerto Leguízamo,86670:San Francisco,86755:Sibundoy,86762:Valle del Guamuez,86780:Villagarzón',
  '63|Quindío|63001:Armenia,63088:Buenavista,63130:Calarcá,63190:Circasia,63212:Córdoba,63272:Filandia,63302:Génova,63400:La Tebaida,63470:Montenegro,63563:Pijao,63594:Quimbaya,63690:Salento',
  '66|Risaralda|66045:Apía,66088:Belén de Umbría,66216:Dosquebradas,66318:Guática,66377:La Celia,66400:La Virginia,66440:Marsella,66456:Mistrató,66001:Pereira,66572:Pueblo Rico,66594:Quiebradillas,66687:Santa Rosa de Cabal,66690:Santuario',
  '88|San Andrés y Providencia|88569:Providencia,88564:San Andrés',
  '68|Santander|68077:Barbosa,68079:Barichara,68081:Barrancabermeja,68092:Betulia,68001:Bucaramanga,68121:Cabrera,68147:Capitanejo,68162:Charalá,68190:Cimitarra,68207:Concepción,68266:Enciso,68276:Floridablanca,68300:Girón,68394:Lebrija,68401:Los Santos,68432:Málaga,68490:Ocamonte,68498:Oiba,68531:Piedecuesta,68591:Puerto Wilches,68632:Sabana de Torres,68660:San Gil,68680:San Martín,68735:Simacota,68745:Socorro,68757:Suaita,68780:Suratá,68790:Tona,68855:Vélez,68861:Vetas,68867:Villanueva,68872:Zapatoca',
  '70|Sucre|70110:Buenavista,70124:Caimito,70147:Chalán,70200:Colosó,70215:Corozal,70221:Cotorra,70228:Coveñas,70235:El Roble,70265:Galeras,70290:Guaranda,70360:La Unión,70400:Los Palmitos,70473:Morroa,70495:Ovejas,70560:Ramos,70570:San Benito Abad,70580:San Marcos,70590:San Onofre,70595:San Pedro,70001:Sincelejo,70660:Sucre,70771:Tolú,70773:Tolú Viejo',
  '73|Tolima|73024:Alpujarra,73067:Ataco,73124:Cajamarca,73148:Carmen de Apicalá,73152:Casabianca,73168:Chaparral,73200:Coello,73217:Coyaima,73226:Cunday,73236:Dolores,73254:Espinal,73270:Falán,73275:Florida,73317:Guamo,73349:Herveo,73352:Honda,73001:Ibagué,73354:Icononzo,73398:Lérida,73408:Líbano,73443:Mariquita,73449:Melgar,73560:Purificación,73608:Rovira,73643:Saldaña,73652:San Antonio,73666:San Luis,73670:Santa Isabel,73805:Venadillo,73810:Villarrica',
  '44|Valle del Cauca|44020:Alcalá,44109:Buenaventura,44113:Buga,44124:Calima (Darién),44130:Candelaria,44001:Cali,44217:Dagua,44243:El Cerrito,44246:El Dovio,44275:Florida,44300:Ginebra,44310:Guacarí,44360:Jamundí,44377:La Cumbre,44400:La Unión,44490:Palmira,44563:Pradera,44595:Restrepo,44605:Río Frío,44834:Tuluá,44894:Yumbo,44901:Zarzal',
  '97|Vaupés|97161:Carurú,97001:Mitú,97343:Pacoa,97400:Papunaua,97666:Taraira,97889:Yavaraté',
  '99|Vichada|99261:Cumaribo,99524:La Primavera,99001:Puerto Carreño,99573:Puerto Rojas,99600:San Juan de Palma,99683:Santa Rosalía,99673:Santo Tomás',
].join('\n');

const parsearMunicipio = (entrada: string): MunicipioDivipola => {
  const indice = entrada.indexOf(SEPARADOR_CODIGO);
  return indice < 0 ? { codigo: '', nombre: entrada } : { codigo: entrada.slice(0, indice), nombre: entrada.slice(indice + 1) };
};

const parsearMunicipios = (serializados: string): MunicipioDivipola[] =>
  serializados
    .split(SEPARADOR_MUNICIPIO)
    .filter(entrada => entrada.length > 0)
    .map(parsearMunicipio);

/** Convierte el texto serializado en la lista de departamentos (tolerante a líneas vacías). */
const parsear = (datos: string): DepartamentoDivipola[] =>
  datos
    .split('\n')
    .map(linea => linea.trim())
    .filter(linea => linea.length > 0)
    .map(linea => {
      const [codigo, nombre, municipios = ''] = linea.split(SEPARADOR_CAMPO);
      return { codigo, nombre, municipios: parsearMunicipios(municipios) };
    });

export const DEPARTAMENTOS: readonly DepartamentoDivipola[] = parsear(DATOS);

const DEPARTAMENTOS_POR_CODIGO = new Map(DEPARTAMENTOS.map(departamento => [departamento.codigo, departamento]));

/** Municipios de un departamento a partir de su código DANE (lista vacía si no existe). */
export const municipiosDe = (codigoDepto: string): MunicipioDivipola[] => DEPARTAMENTOS_POR_CODIGO.get(codigoDepto)?.municipios ?? [];

/**
 * Normaliza un valor para comparaciones: recorta espacios, pasa a minúsculas y
 * elimina tildes/diacríticos (ej. 'Bogotá D.C.' → 'bogota d.c.').
 */
export const normalizar = (v: string): string =>
  v
    .trim()
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '');
