# G3-03 — Мультиплеер, безопасность, каталоги и происхождение

**2026-11-18 — PROPOSED / NOT CODE VALIDATED.** [G2-10](../03-system-design/G2_10_UI_MULTIPLAYER_COMMAND_AUTH.md), [D-043/D-044/D-046/D-048](../../DECISIONS.md).

## Сеть и разрешения

Клиент пересылает typed request `CommandId, ActorSession, TargetId, ExpectedRevision, Payload`. Сервер восстанавливает ActorId из authenticated session, **не** доверяет указанному в сообщении лидерству, цене, TechUnlocked, местоположению NPC или праву на участок. `G2-10 ACL` контролирует каждую изменяющую операцию: участки, сундуки, казна, tech, war, claims, гильдии. `STALE_REVISION` сообщает пользователю причину и необходимость обновления. Для неизвестного contentId отказ, а не автоматическое создание.

### Приватность и сетевой протокол

Раздельные read models: публичная карта, сведения жителя, собственная экономика государства, военная разведка и административная диагностика. Чужие закрытые склады/шпионские приказы не выдавать без разрешения. Конкретные API/Minecraft network versions устанавливаются G3 feasibility, сетевые размеры и rate limits benchmarked.

## Каталоги: source label != implementation claim

`ContentDefinition`: `ContentId, SourceRevision, EvidenceConfidence, EffectKind, PrerequisiteIds, CostPolicy, TargetPatch, ImplementedState, ApprovalRef`.
Статусы: `SOURCE_VERIFIED`, `SOURCE_PARTIAL`, `SOURCE_UNKNOWN`; `APPROVED_DESIGN` отдельно от `IMPLEMENTED_AND_TESTED`. Source version V1.0.9 diagram задаёт приоритет структуры, поздняя wiki — отдельно проверенные расширения. Запрет авторелиза 73 зданий и 28 чудес по одному списку.

## Provenance and file rights

До добавления чужого файла: автор, источник, лицензия **конкретного файла**, вендор, обязательная атрибуция, dependency chain и хранение notices. `RIGHTS_UNVERIFIED` блокирует импорт; собирать независимые ассеты и новый код. Root GPL-2.0 и notices AVRGAMING остаются фактическим конфликтом G0, не юридически разрешённым фактом. При разрешении автора или совместимой лицензии — отдельная проверка обязательств.

## Threats и тесты

SEC-01 клиент подделывает администратора/мэра; SEC-02 двойная покупка; SEC-03 пакет fake tech completion; SEC-04 hostile spy read leak; SEC-05 packet replay after restart; SEC-06 compromised/unknown content data; SEC-07 maliciously large NBT payload; SEC-08 unauthorized remote claim; SEC-09 actor disconnected midway transaction; SEC-10 guild tries state privileges.

**OPEN:** конкретные network codecs/permissions integration, test runner, dependency license assessment, mapping/Java versions. Требуется реальная проверка.