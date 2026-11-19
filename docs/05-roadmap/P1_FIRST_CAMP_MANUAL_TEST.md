# P1 Camp Heart v1 — manual gameplay acceptance

**Development PR:** #9 into feat/p1-living-settlement. **Do not merge/release without successful Minecraft 26.3 client acceptance.**
**World rule:** create a NEW disposable CivCraft-enabled world; back up any old world first. Existing historical 4-obsidian camps are not automatically migrated.

## First-camp player flow

1. Start a Fabric 26.3 client with matching development mod and server dependencies, enable a CivCraft world.
2. Craft the existing camp core item (renamed **«Основание лагеря»**; identifier remains `civcraft_reborn:camp_core`).
3. Place it on flat, cleared supported ground. The full cyan Heart hologram should appear automatically with the real foundation inside it. At this stage NO registered camp exists.
4. Press **G** (rebindable in Controls) to open the **Лагерь / Цивилизация** interface or press Esc and click the dedicated **CivCraft** button. Right-click on the foundation MUST NOT open this general menu.
5. Under `Лагерь → Схема строительства → Сердце лагеря`, move the footprint up to ±2 blocks around the fixed core and rotate ±90°. The actual core cannot teleport. Display and server should agree; invalid overlaps/terrain/unloaded chunks must be refused.
6. Click **«Подтвердить площадку лагеря»**. Server stores owner/world/transform. Further reposition must be refused.
7. Open **«Материалы и остаток»** to see required block resources (a door is one item but two block positions). Material list is advisory; player constructs by placing physical Minecraft blocks manually.
8. Verify completed hologram cells disappear, including door halves and oriented roof stairs. Wrong blocks don't count; removing a correct block makes its ghost reappear.
9. Run `/civcraft camp heart inspect` (nearby). Expected **162/162** for the accepted complete structure.
10. Open the menu and click **«Основать лагерь»** (or `/civcraft camp found`): before 162/162 it must fail; afterward create ONE permanent CampId with no second material debit.
11. Verify the new camp menu works from a distance. Recruitment requires actual nearby living villagers and validated housing; no free NPCs are spawned.
12. Leave/rejoin and restart the server. Confirm the site transform and ownership, construction progress, CampId, and the ability to repair after damaging any structural required block.
13. Check multiplayer rights and foreign core attempts, finite world border, other dimensions, unloaded edge chunks, and GPU stability when showing/hiding/switching STORAGE and HEART projections.

## Known limitations in this draft

- Clicking the dedicated CivCraft button is implemented **in the pause menu**; the persistent clickable in-world HUD button is NOT yet implemented. The G hotkey works as a separate input path.
- Preexisting worlds with a legacy core/obsidian-cross foundation require a migration or conversion command.
- A legacy reward of 4 obsidian per foundation placement remains in code, despite the new 7x7 Heart not requiring obsidian. Owner balance decision is outstanding.
- Numerical `hammers/hour`, shared production allocation, and fully foundation-anchored NPC construction of other buildings are NOT part of this first-camp vertical slice; legacy NPC builder code still needs redesign.
- Exact Minecraft GPU rendering and controls cannot be certified by headless CI.
- On first-pass client UX, error feedback on a rejected move may temporarily require reopening the placement screen to resynchronize; server remains authoritative.

## Build validation

Review GitHub Actions P1 compile-and-server and P0 checks for the latest PR head. Require Java 25, unit tests, dedicated bootstrap and actual Minecraft 26.3 client graphics acceptance. A green CI run is necessary but not sufficient.

## Исправление отсутствующего завершения строительства (2026-11-19)

**Причина наблюдаемого поведения:** раньше подтверждённый проект не имел сохранённого серверного состояния `COMPLETED` — каждые 10 тиков выполнялся подсчёт и (при 100%) показывалось только сообщение онлайн-владельцу. Кроме того, кнопка **«Проверка лагеря»** ошибочно вызывала `/civcraft camp inspect`, который требует уже зарегистрированного лагеря и потому не может проверять начальную стройку.

**Исправление:**
1. `CampHeartSite.completed` — сохраняемый серверный признак, по умолчанию `false` для старых сохранений. В загруженном мире сервер автоматически переводит подтверждённую постройку в `COMPLETED` при 100% точного совпадения и обратно в `DAMAGED`, когда обязательный блок повреждён. Выгруженные чанки не изменяют статус.
2. Кнопка **«Проверить постройку»** в основном меню вызывает правильную команду `/civcraft camp heart inspect`. Она показывает `X/162`, состояние `FINISHED` или `INCOMPLETE` и до 8 точных мировых координат несовпадающих блоков с ожидаемыми/фактическими BlockState.
3. Команда `/civcraft camp found` дополнительно сообщает первую ошибочную координату и не предъявляет повторного требования твёрдого грунта под центром при уже полностью проверенном здании на поднятой площадке.
4. **Завершение постройки НЕ означает автоматическое основание государства.** При `162/162` игрок получает уведомление и отдельным нажатием **«Основать лагерь»** регистрирует CampId. Это два явно разделённых игровых этапа.

### Минимальная приёмка в клиенте

- Открыть G → **«Проверить постройку»**. Если не 162/162, устранить координаты из сообщения, включая ориентацию ступеней, верхнюю/нижнюю часть двери и верхнюю плиту.
- После 162/162 в чате должно появиться **«строительство сердца лагеря ЗАВЕРШЕНО»**; при разборке одного блока — сообщение о повреждении, после ремонта — снова завершение.
- Нажать **«Основать лагерь»** и убедиться в появлении CampId. Старые проекты должны оставаться доступными без переустановки стартового блока.
- Сохранить мир, перезайти, проверить сохранение статуса и отсутствие ложного завершения при незагруженном чанке.

## Комплексная приёмка пользовательского пути P1.2 — ноябрь 2026

1. Создать **новый** мир с включённой опцией «Мир CivCraft» и проверенными настройками границ. В обычном мире игра не должна разрешать основание лагеря.
2. В выживании скрафтить `civcraft_reborn:camp_core` по рецепту 4 дубовых бревна, 2 железных слитка, 2 булыжника, 1 костёр. Сравнить с JSON-рецептом в ресурсах мода.
3. Установить основание, открыть `G` → «Схема строительства» → «Сердце лагеря». Если в `index.json` больше одного варианта, выбрать другой чертёж перед подтверждением.
4. Открыть F7: проверить полупрозрачные реальные блоки, вращение вида, перетаскивание ЛКМ, ориентацию схемы R, Page Up/Page Down, выход по Esc/F7. Персонаж не должен двигаться при нажатиях клавиш проектирования.
5. Попробовать разместить дом на неровном участке и поверх дерева. Подтверждение не разрушает настоящие блоки, а неправильные позиции помечены красным. Для схемы с `--require-air` очищаемые клетки показываются отдельными красными отметками.
6. Подтвердить проект; инициатор исчезает, а версия `blueprint_id`, позиция, высота и поворот сохраняются в SavedData. Убедиться, что изменение варианта после подтверждения запрещено.
7. Строить вручную, периодически проверять `/civcraft camp heart inspect`. До 100% команда выдаёт **мировые координаты** и точные ожидаемые BlockState (включая направление ступеней/часть двери). Голограмма правильной ячейки исчезает.
8. При полном совпадении мод должен сохранить `completed=true`, показать сообщение о завершении, а в меню G — значение N/N и активную кнопку «Основать лагерь».
9. Нажать «Основать лагерь»; сервер должен создать CampId без повторного списания материалов. Перезайти в мир и проверить наличие CampId, прежней версии чертежа и установленного статуса.
10. В рамках развития построить жилые помещения и склады, обеспечить 3 комнаты, 3 настоящих жителей, 16 единиц еды, мастерскую, ферму. Во вкладке «Цивилизация» на текущем этапе отображаются полученные с сервера показатели, а не вымышленные ресурсы. Проверить повышение поселения.
11. Для пользовательской структуры: сохранить отдельный дом Structure Block в `.nbt`, выполнить `python scripts/import_structure_nbt.py ...`, собрать JAR и установить одинаковые ресурсы на клиент и сервер. Проверить, что старый проект `camp_heart_v1` не подменяется новым.
12. Удалить из пакета выбранный авторский шаблон после создания отдельного проекта и открыть мир: **сервер не должен падать или молча менять схему**. Для восстановления необходимо вернуть файл той же версии.

**Проверка CI недостаточна:** GUI, ввод, альфа-смешивание, реальное строительство, импорт из внешнего творчества и полный игровой цикл должны проходить в Minecraft 26.3. До такой приёмки статус MVP остаётся экспериментальным.
