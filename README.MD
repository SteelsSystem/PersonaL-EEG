b12,"9 725,38","9 646,59","+0,81 %","2,98","2,88","+3,47 %","154,98","154,43","+0,35 %"
b15,"53 432,06","53 134,15","+0,56 %","26,32","26,32","0,00 %","654,93","657,43","-0,38 %"
b17,"171 193,62","170 264,84","+0,54 %","32,47","31,14","+4,27 %","2 015,70","2 011,30","+0,22 %"
s35932,"111 402,38","111 088,12","+0,28 %","14,13","10,84","+30,35 %","2 290,20","2 328,40","-1,67 %"
s38417,"107 803,98","107 349,70","+0,42 %","20,84","16,69","+24,87 %","1 949,00","1 949,60","-0,03 %"
s38584,"87 647,35","87 229,18","+0,48 %","15,38","13,11","+17,32 %","1 572,10","1 570,90","+0,08 %"
Mimořádně nízké hodnoty režie plochy a výkonu potvrzují praktickou použitelnost architektury i pro průmyslové návrhy s přísnými rozpočty.
4. Rigorózní bezpečnostní verifikace: Odolnost vůči SAT a ATPG útokům (Security Verification)
Bezpečnostní architektura skrytých hradel a K-Gate Locku je postavena na eliminaci základního předpokladu všech orákulem vedených de-kamuflážních útoků: útočník nedokáže z layoutu ani mikroskopických snímků určit, které buňky jsou kamuflážní a které jsou běžnými standardními prvky. V důsledku toho musí útočník považovat všechna hradla v obvodu za podezřelá, což způsobuje explozivní nárůst vyhledávacího prostoru.
Analýza SAT a AppSAT Útoků
Při formulaci SAT útoku nahrazuje útočník každé podezřelé hradlo s  $N$  piny permutační sítí pinů (Pin Permutation Network) řízenou proměnnými klíče. Pro hradlo s  $N$  piny je celkový počet platných permutací fiktivních pinů  $P$  definován jako:
$$P = \sum_{n=1}^{N-1} \binom{N}{n}, \quad \text{kde } 1 < n < N$$
Délka klíče  $|K|$  vyžadovaná pro zakódování permutační sítě jednoho hradla činí:
$$|K| = \log_2(P + 1)$$
Například u 3-vstupového NAND hradla existuje  $\binom{3}{1} + \binom{3}{2} = 6$  (resp. 7 platných konfigurací při započtení plné závislosti), což vyžaduje 3 bity klíče. Jelikož útočník netuší, které buňky jsou skryté, musí zakódovat kompletní netlist.
Exponenciální rozšiřovací prostor vede k selhání SAT solvers v definovaném limitu (Timeout 12 hodin):
| Benchmark | Počet hradel | Původní kamufláž (5% NAND/NOR/XOR)  $|K|$  | Původní SAT čas (s) | Skrytá hradla (All Gates Encoded)  $|K|$  | Skrytý SAT čas (hod.) MUX Form | AppSAT Čas (hod.) | | :--- | :--- | :--- | :--- | :--- | :--- | :--- | |  C1908  | 880 | 34 | 0,55 s | 811 |  3,52 h  5,91 h | N/A | |  C2670  | 1 193 | 26 | 0,65 s | 1 514 |  Timeout (>12 h)  Timeout |  Timeout (>2 h)  | |  C3540  | 1 669 | 28 | 0,68 s | 2 088 |  Timeout (>12 h)  Timeout |  Timeout (>2 h)  | |  C5315  | 2 307 | 46 | 3,58 s | 3 379 |  Timeout (>12 h)  4,27 h |  Timeout (>2 h)  | |  C7552  | 3 512 | 106 | 4,07 s | 4 454 |  Timeout (>12 h)  Timeout |  Timeout (>2 h)  | |  arbiter  | 11 839 | 1 182 | 3 815,00 s | 23 678 |  Timeout (>12 h)  Timeout |  Timeout (>2 h)  | |  voter  | 13 758 | 1 078 | Timeout (>12 h) | 21 560 |  Timeout (>12 h)  Timeout |  Timeout (>2 h)  |
Průnik  AppSAT (Approximate SAT)  útoků rovněž selhává. AppSAT předčasně ukončuje vyhledávání, pokud chybovost (error rate) klesne pod nastavený práh. U skrytých hradel však nedochází k plynulému poklesu chybovosti, protože skrytá hradla zavádějí funkční corruptibilitu bez strukturálních stopy zanechávajících prvků (point functions). Výkonnostní krajina chyby nedisponuje hladkým gradientem, čímž znemožňuje konvergenci přibližného řešiče.
Analýza testovacích útoků na bázi ATPG (Test-Based Attack Review)
Útočník využívající komerční nástroje pro generování testovacích vzorů (např. Synopsys TetraMAX) aplikuje dvoukrokový model poruchy trvalého nastavení (stuck-at fault model):
Senzitizace (Sensitize):  Nastavení testovaného pinu na řídicí hodnotu (controlling value – logická 0 pro NAND/AND, logická 1 pro NOR/OR) nastavením poruchy stuck-at-1 (resp. stuck-at-0).
Propagace (Propagate):  Nastavení neřídicích hodnot (non-controlling values) na všechny ostatní piny hradla i na celé cestě k pozorovatelnému bodu (primary output nebo scan flip-flop).
V reálných netlistech však ATPG útoky narážejí na vznik  ATPG netestovatelných poruch (ATPG Untestable Faults)  a vzájemné rušení fiktivních pinů v cesto-kónu.
Vyhodnocení úspěšnosti ATPG útoku na rozsáhlém sekvenčním benchmarku  b18  (84 632 primitiv, 3 020 scan flip-flopů):
Typ hradla,Počet hradel v b18,Detekovatelné piny (#),Detekovatelné piny (%),Nedetekovatelné piny (#),Nedetekovatelné piny (%),ATPG Netestovatelné (#),ATPG Netestovatelné (%),Not Detected / Limit (#),Not Detected / Limit (%)
NOR2X,2 390,10,"0,42 %",5,"0,21 %",2 373,"99,29 %",2,"0,08 %"
NOR3X,270,12,"4,44 %",0,"0,00 %",237,"87,78 %",21,"7,78 %"
NOR4X,195,17,"8,72 %",0,"0,00 %",114,"58,46 %",64,"32,82 %"
NAND2X,4 194,7,"0,17 %",30,"0,72 %",4 154,"99,05 %",3,"0,07 %"
NAND3X,2 135,8,"0,37 %",19,"0,89 %",1 849,"86,60 %",259,"12,13 %"
NAND4X,909,38,"4,18 %",0,"0,00 %",753,"82,84 %",118,"12,98 %"
Vysoký podíl nedetekovatelných a ATPG netestovatelných pinu (> 99 % u 2-vstupových hradel) dokazuje, že matematické generování testovacích vzorů pro izolaci fiktivních pinů je v praxi neproveditelné.
5. Protokol nasazení, integrace do netlistu a plán vydání (Deploy Protocol & Release Roadmap)
Pro bezpečné začlenění skrytých hradel a víceklíčového zamykání logiky K-Gate Lock do standardního automatizovaného návrhového toku (CAD/EDA Synthesis Flow) je nutné dodržet prováděcí pravidla integrace netlistu a harmonogram přípravy výroby.
Pravidla integrace netlistu (Netlist Integration Commands)
Striktní zákaz kombinačních zpětných smyček (Combinational Feedback Loops):  Při výběru fiktivních netů (dummy nets) určených k připojení na fiktivní vstupy skrytých hradel  je striktně zakázáno vytvářet kombinační zpětné smyčky . Kombinační smyčky vyvolávají nežádoucí oscilace a porušují pravidla statické časové analýzy (STA). Útočník by tyto struktury okamžitě identifikoval jako porušení návrhových pravidel (DRC check), čímž by byla kamufláž odhalena. Sekvenční zpětné smyčky (přes klopné obvody) jsou plně povoleny.
Deklarace falešných časových cest (False Timing Paths):  V nástroji Synopsys Design Compiler musí být na fiktivní piny skrytých buněk přikázána instrukce set_false_path. Tím se zabrání tomu, aby syntetizátor nadbytečně optimalizoval zpoždění na neexistujících logických cestách, čímž se zachová přesná časová dynamika reálného obvodu.
Vyrovnání statistických metrik (Signal Probability Skew):  Pozice skrytých hradel musí být voleny s ohledem na zachování statistických metrik přechodu signálu. Pravděpodobnost přepínání fiktivních netů musí odpovídat běžným hradlům v daném logickém kónu, aby se zabránilo statistickému vyčlenění pomocí nástrojů strojového učení (ML classifiers).
Harmonogram vydání a strategické cíle (Release Goal & Roadmap)
Fáze 1: Syntéza a optimalizace netlistu (CAD Flow Setup)  Alokace rozpočtu režie v nástroji Synopsys Design Compiler. Konverze vybraného podílu buněk (např. 20 % invertorů na Skrytá NAND2X2 a NOR2X2 hradla) při aplikaci příkazů set_false_path na fiktivní piny a kontrola absence kombinačních zpětných smyček.
Fáze 2: Fyzikální maskování a implantace (Foundry Preparation)  Generování přesně 3 dodatečných fotomasek v semiconductor fabu: dvě masky pro plytkou iontovou implantaci kanálů (< 10 nm) u Always-On tranzistorů (PMOS Bor 2 keV, dávka  $5 \times 10^{13} \text{ cm}^{-2}$ ; NMOS Fosfor 5 keV, dávka  $5 \times 10^{13} \text{ cm}^{-2}$ ) a jedna maska pro nanášení izolační  $\text{SiO}_2$  vrstvy u Always-Off tranzistorů.
Fáze 3: Víceklíčové kódování K-Gate Lock (Logic Encoding)  Implementace kombinační kódovací logiky pro vybraná hradla na úrovni zamykání  $k$  a integrace sekvenčního řídicího protokolu pro přivádění časových sekvencí klíčů v provozních stavech obvodu.
Fáze 4: Verifikace a akceptační testy (Sign-off & Qualification)  Ověření absence parazitních svodových cest a simulace statického proudového odběru  $I_{DDQ}$ . Spuštění verifikačních testů vůči SAT a ATPG útočným nástrojům pro potvrzení kompletní neprolomitelnosti obvodu.
Kombinace fyzikálně nediferencovatelných skrytých hradel na tranzistorové úrovni a víceklíčového dynamického logického zamykání K-Gate Lock vytváří absolutní strategickou obrannou linii. Tato metodika poskytuje 4. generaci ochrany polovodičového duševního vlastnictví, která spolehlivě deklasuje invazivní mikroskopické útoky i pokročilé orákulem vedené logické řešiče.
 
 
Claude is responding
