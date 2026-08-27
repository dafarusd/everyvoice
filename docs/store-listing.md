# Store listing copy

Draft copy for Google Play and F-Droid. Character limits are Play's.
Nothing here claims the app helps anyone communicate better — that claim
needs evidence this project does not have yet.

The declared target audience on Play is 13+, so this copy does not present
children as the audience. It is written for the adult setting the app up.
The app is of course used by younger people; that is the parent's call, not
a claim the listing makes.

---

## App name (30 char limit)

```
EveryVoice
```

## Short description (80 char limit)

```
Tap a word, the phone speaks. Free, offline, no ads, no account.
```

## Full description (4000 char limit)

```
EveryVoice is a free communication app for people who cannot rely on speech.

Tap a word or a picture and the phone says it out loud. Tap several and it
speaks the whole sentence. It is for anyone who is nonverbal or minimally
verbal — after a stroke, with ALS, cerebral palsy, Down syndrome, autism, or
a brain injury.

EveryVoice is set up by the person supporting them: a family member, a
caregiver, or a therapist, who chooses the words and arranges them.

It costs nothing, it works with no internet connection, and it asks for no
account.

WHAT IT DOES

• 321 starter words and phrases across 13 categories, chosen so a person can
  direct their own life rather than only request things. Stop. No. Help. I
  need the bathroom. I am in pain. Leave me alone. I don't understand.

• A sentence strip. Tap words to line them up, then one press speaks the
  whole sentence.

• Search. Type a few letters to reach a word without paging through
  categories.

• Caregiver edit mode. Add, change and delete buttons. Put a photo on a
  button with the phone's camera, or an emoji, or plain text.

• Adjustable grid — 2, 3, 4 or 6 columns — for different motor abilities.

• Backup and restore to a single file. A customised vocabulary can be months
  of work; it should survive a lost phone.

• Works on Android 7.0 and later, on old and inexpensive phones.

NOTHING LEAVES YOUR DEVICE

EveryVoice does not request internet permission. Android will not let an app
without that permission open a network connection at all. There is no
account, no analytics, no advertising, and no tracking of any kind.

The one exception is honest and worth knowing: the voice comes from your
phone's own text-to-speech engine, which is separate software. Most phones
speak entirely offline. If yours is set to synthesise speech over the
network, the sentence goes to that engine's servers. Settings →
Accessibility → Text-to-speech output will tell you which you have.

WHAT THIS IS NOT

This is not a medical device. It has not been approved by any regulator and
has not been evaluated in a clinical trial. It is not therapy and it does not
replace a speech-language pathologist.

Research is consistent that whether AAC succeeds depends more on the people
around the user — and on being taught to model language on it — than on which
app is installed. This app removes a price barrier. It does not remove the
need for that person.

If you can reach a speech-language pathologist, go. If you are waiting, and
many families wait months, this is meant to be useful in the meantime rather
than instead.

OPEN SOURCE

The complete source code is public under the GNU Affero General Public
License v3.0. You do not have to trust the claims above — you can read the
code, build it yourself, and check the permissions in the file you install.

https://github.com/dafarusd/everyvoice

Bug reports are welcome, and bad news about this app is more useful than
good news.
```

---

## Category and tags

- **Category:** Medical is wrong (it is not a medical device). Use
  **Health & Fitness** or **Communication** — Communication fits what the app
  actually does and avoids implying clinical status.
- **Tags:** AAC, communication, accessibility, speech, nonverbal, offline

## Content rating

Answer the questionnaire honestly: no violence, no user-generated content
shared with others, no data collection, no ads, no purchases. It should come
back as suitable for everyone.

## Data safety form

Every answer is "no":

- Data collected: **none**
- Data shared: **none**
- Data encrypted in transit: not applicable — no data is transmitted
- Users can request deletion: not applicable — nothing is held

Add a note in the free-text field pointing to PRIVACY.md and to the fact
that the app declares no `INTERNET` permission, which is checkable from the
APK.

## Required URLs

| Field | Value |
|---|---|
| Privacy policy | https://github.com/dafarusd/everyvoice/blob/main/PRIVACY.md |
| Support | https://github.com/dafarusd/everyvoice/issues |

## Assets

- `store/icon-512.png` — 512 × 512, 32-bit
- `store/feature-1024x500.png` — 1024 × 500, no alpha
- `screenshots/01-categories.png` and `screenshots/02-sentence.png` — 1080 × 2340

All four generated or captured; see `make-store-assets.py` for the first two.

## What's new (release notes for 0.1.0)

```
First public release.

321 words and phrases, sentence strip, search, caregiver editing with
photos, adjustable grid, backup and restore. No internet permission, no
account, no ads.

Not yet tested with AAC users or reviewed by a speech-language pathologist.
Please report anything that is broken.
```
