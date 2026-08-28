# EveryVoice

**Point. Tap. Be heard.**

EveryVoice is a free, offline, open-source AAC app for Android. AAC —
augmentative and alternative communication — is the field of tools that
speak for people who are nonverbal or minimally verbal: autistic children,
stroke survivors with aphasia, people with ALS, cerebral palsy, Down
syndrome, or anyone who cannot rely on speech.

Tap a button, the phone speaks. String buttons into a sentence, one press
says it all. No account. No internet. No ads. No tracking. Nothing leaves
the device, ever.

## Why this exists

The leading AAC apps cost around $250–$300, and dedicated speech devices
have historically run into the thousands. Access is gated by insurance,
school districts, and geography — while a used Android phone costs less
than a restaurant meal. The vocabulary research is unambiguous: a few
hundred core words cover the large majority of everyday communication.
The barrier was never knowledge. It was price. So the price is now zero.

## What it does (v0.1)

- **13 categories, 300+ core words and phrases**, seeded from published
  core-vocabulary research: Core, Phrases, People, Actions, Food & Drink,
  Feelings, Body, Places, Questions, Social, Time, Describing, Things.
- **Sentence strip.** Taps queue into a strip; one press speaks the whole
  sentence. Individual tiles also speak on tap.
- **Search.** Type a few letters; ranked exact → prefix → contains.
- **Caregiver edit mode** (behind a gate dialog): add, edit, delete tiles.
  Add a photo with the camera, an emoji, or text only.
- **Adjustable grid** (2/3/4/6 columns) for different motor abilities.
- **Backup and restore** to a single ZIP file. A customized vocabulary is
  months of work; it survives phone replacement.
- **Says why it is silent.** A phone with no installed voice gets a banner
  and a button that opens the voice installer, rather than buttons that
  light up and never speak.
- **Fully offline.** The manifest has no `INTERNET` permission. That is a
  promise the compiler enforces, not a policy a company can quietly change.

## What it is not

- **Not a medical device and not therapy.** AAC works best alongside a
  speech-language pathologist. This app removes the price gate; it does
  not replace professional guidance.
- **Not yet symbol-based.** v0.1 uses emoji and caregiver photos rather
  than a licensed symbol library (PCS/Boardmaker symbols are proprietary).
  Bundling an open symbol set (Mulberry, CC BY-SA) is planned.
- **Not user-verified.** As of v0.1 the app is built, unit-tested, and
  device-verified on one phone — but it has not been tested with a single
  real AAC user, and no speech-language pathologist has reviewed it. That is
  the next milestone, and claims about effectiveness wait for it.

## Build

Requirements: JDK 17, Android SDK (platform 35, build-tools 34+).

```
./gradlew :app:assembleDebug      # builds app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:testDebugUnitTest  # runs the JVM unit tests
```

Stack: Kotlin 2.0.21, Jetpack Compose (BOM 2024.12.01), Room 2.6.1,
AGP 8.7.3, Gradle 8.9. minSdk 24 (Android 7.0, 2016 — covers roughly 97%
of active devices).

## Verification status

| Layer | Status |
|---|---|
| Engine (sentence strip, search, vocabulary seed, tile identity) | 32 JVM unit tests passing |
| App packaging | Debug APK builds clean (9.4 MB), no network permission in the built artifact |
| Speech output | Device-verified on a Galaxy A15 (Android 16): speaks aloud from an on-device voice, no network |
| Backup and restore | Device-verified: full round trip through the system file picker, including a deliberately corrupted file |
| Camera photo tiles, missing-voice banner | SOURCE-COMPLETE, NOT device-verified |
| Clinical effectiveness | NOT claimed. Requires testing with AAC users and SLP review |

Tested on one phone, by one person who is not an AAC user. That is the floor,
not the bar.

## Design rules that will not change

1. No accounts, no network permission, no analytics, no ads.
2. Caregiver data stays on the device unless the caregiver exports it.
3. Backups are a core feature, never an afterthought.
4. Vocabulary is editable; the app never locks a user's words away.
5. Big targets, high contrast, no gestures a motor-impaired user can't do.

## Roadmap (rough order)

1. Device testing on a real phone; fix what breaks.
2. Mulberry symbol set (CC BY-SA) bundled for core tiles.
3. Multilingual vocabularies (strings are externalized; seed data is not yet).
4. Switch-access scanning for users who cannot touch the screen.
5. Fixed motor-planning layouts (words never move position).
6. Play Store / F-Droid release — preceded by testing with real users.

## Install it

Download `everyvoice-0.1.0.apk` from the
[latest release](https://github.com/dafarusd/everyvoice/releases/latest) and
open it on the phone. Android will ask you to allow installing from that
source — that is the normal prompt for an app that does not come from the
Play Store.

Or build it yourself from the instructions above. The APK in the release is
signed with the project's upload key, so an APK you build will not install
over it; uninstall first, and export a backup before you do.

## Please read before relying on it

[DISCLAIMER.md](DISCLAIMER.md) — not a medical device, not therapy, not a
replacement for a speech-language pathologist, and not yet tested with a
single AAC user.

[PRIVACY.md](PRIVACY.md) — what stays on the device, and the one thing that
is not ours.

## License

GNU Affero General Public License v3.0 (AGPL-3.0-only). See
[LICENSE](LICENSE) and [NOTICE](NOTICE).

The starter vocabulary is original, compiled from published core-vocabulary
research. No proprietary symbol set is bundled — see NOTICE. Emoji render
from the device's own font and carry that font's license.

---

Built by Dafarus — [@Dafarusd on X](https://x.com/Dafarusd)
