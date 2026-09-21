---
name: Kotlin and Compose
description: Style and architecture constraints for Kotlin and Jetpack Compose code.
activation: glob
globs: ["**/*.kt", "**/*.kts"]
---

# Kotlin and Compose

## Architecture

- MVVM + unidirectional data flow. Events go up, state comes down.
- One `UiState` data class per screen, immutable, exposed as
  `StateFlow<UiState>` from the ViewModel. No multiple parallel flows for one
  screen.
- Repositories are the only thing that talks to Room or Supabase. ViewModels
  never touch a DAO or an HTTP client directly.
- Room is the source of truth for anything cached. Network writes to Room, UI
  reads from Room. Do not return network models to the UI.
- Domain models live in `core-model` and contain no Android or framework types.
  Map DTO → domain → UI explicitly; never reuse a DTO as a UI model.

## Compose

- Composables are stateless and take `Modifier` as the first optional param.
- Hoist state. A composable that owns business state is a bug.
- No `LaunchedEffect(Unit)` for anything that should be in the ViewModel.
- Lists use `key = { it.id }` in `items`. Missing keys cause scroll jank and
  wrong animations.
- Every screen-level composable has a `@Preview` for light and dark, plus one
  at 320dp width.
- Images load through Coil with a blurhash or solid placeholder. Never a blank
  box that resizes when the image arrives.
- Bangla text must use the bundled Noto Sans Bengali typography tokens from
  `core-ui`. Never `FontFamily.Default` for Bangla.

## Coroutines

- `viewModelScope` in ViewModels, injected scope elsewhere. No `GlobalScope`.
- Every suspend function that does work takes a `CoroutineDispatcher` with a
  default, so tests can inject a test dispatcher.
- `Flow` collection in Compose uses `collectAsStateWithLifecycle()`.
- Catch exceptions at the repository boundary and convert to a sealed `Result`.
  Do not let raw exceptions reach the ViewModel.

## Banned

- `!!`, `lateinit var` in ViewModels, `runBlocking` outside tests.
- `Thread.sleep`, `System.currentTimeMillis()` for business logic — inject a
  `Clock`. This matters: content unlocking must not depend on device time.
- Hardcoded strings, colors, dimensions. Use `strings.xml` and `core-ui` tokens.
- `@Suppress` added to silence a lint error you caused.
- Reflection, and any `kotlinx.serialization` model without `@Serializable`.

## Tests required with the code

- ViewModel: state transitions for loading, success, empty, error.
- Repository: offline path returns cached data when the network fails.
- Mapper: DTO → domain round trip.
- Pure logic (quiz generator, streak calculator, day index): property tests, not
  just examples.

Use JUnit5 + Turbine + MockK. A step is not done if its ViewModel has no test.
