# Privacy Policy — EveryVoice

Last updated: 26 August 2026

EveryVoice is a communication app for people who cannot rely on speech. The
people who use it are often children, or adults after a stroke or a
diagnosis. What they say through it is as private as anything a person says.

**EveryVoice collects nothing, sends nothing, and stores nothing anywhere but
your own device.**

That is not a promise about our intentions. It is a property of the app you
can check yourself, and this policy tells you how to check it.

## No internet permission

The app does not request the `INTERNET` permission. On Android, an app
without that permission cannot open a network connection at all — the
operating system refuses. It is not a setting, a policy, or a toggle that can
be changed later without you installing a new version and seeing the
permission change.

You can verify this yourself:

- On the phone: Settings → Apps → EveryVoice → Permissions
- From the APK: `aapt2 dump permissions app-release.apk`
- In the source: [`app/src/main/AndroidManifest.xml`](app/src/main/AndroidManifest.xml)

The only permission the app declares is one Android itself adds so the app
can talk to its own internal components.

## What stays on your device

| What | Where it lives |
|---|---|
| Your words, categories and buttons | A database in the app's private storage |
| Photos you put on buttons | The app's private storage, invisible to other apps |
| Which grid size you chose | In memory, for the current session |

Android's private app storage is not readable by other apps. Uninstalling
EveryVoice deletes all of it.

## Backups go where you send them

"Back up vocabulary" writes a single ZIP file to a location **you** pick
through Android's file picker. The app does not choose the destination, does
not upload it, and does not keep a copy anywhere else. If you save it to a
cloud folder, it goes to that cloud because you put it there.

The backup contains your words and your photos. Treat it as you would a
photo album.

## The camera

Adding a photo to a button opens **your phone's own camera app**. EveryVoice
does not request camera permission and never sees the camera directly. The
photo you take is written into EveryVoice's private storage and the working
file is deleted immediately after.

## The one thing that is not ours — the voice

EveryVoice does not contain a voice. It asks Android's text-to-speech engine
to speak, the same engine used by every other app on the phone.

**That engine is separate software with its own permissions and its own
privacy policy.** Most phones ship with an on-device voice, and in that case
the words never leave the phone. But some text-to-speech engines can be
configured to synthesise speech over the network, and if yours is set that
way, the sentence being spoken goes to that engine's servers — not to us, and
not because of anything EveryVoice does.

If this matters to you, check Settings → Accessibility → Text-to-speech
output and choose an engine with installed, offline voice data. We cannot
check it for you, and we would rather say so than let you assume.

## No accounts, no analytics, no ads

There is no sign-up, no login, no account. There is no analytics library, no
crash reporter, no advertising SDK, and no third-party code that phones home.
The full dependency list is eleven lines of
[`app/build.gradle.kts`](app/build.gradle.kts) and every entry is an Android
system library.

## Children

EveryVoice is used by children. Since the app collects no data at all, there
is no personal information from a child to collect, store, share, or sell —
by us or by anyone else.

## Changes

If a future version ever needed network access, it would require the
`INTERNET` permission, which Android shows you at install time, and this
policy would change before it shipped. The commit history of this file is
public.

## Contact

Open an issue at https://github.com/dafarusd/everyvoice/issues

---

EveryVoice is free software under the GNU Affero General Public License v3.0.
The complete source is public, which means you do not have to take any of the
above on trust.
