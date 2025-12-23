FLUJO DE TRABAJO PREMIUM DEFINITIVO
Flutter · Local-First · Idempotente · Play Store Ready
End-to-End: Idea → Código → Build → AAB
0. Regla Suprema del Sistema
Todo el sistema es determinista, reproducible e idempotente.
Ejecutar el flujo completo 1 o 100 veces produce el mismo resultado final.
No backend.
No nube.
No tracking.
No improvisación.
1. Arranque del Proyecto (Primera Letra)
1.1 Crear proyecto Flutter (único comando permitido)
Copiar código
Bash
flutter create flutter_local_app
cd flutter_local_app
1.2 Limpieza inicial (obligatoria)
Eliminar código demo
Eliminar comentarios innecesarios
Mantener solo estructura base
2. Estructura FINAL del Proyecto (Bloqueada)
Copiar código

flutter_local_app/
│
├── .github/
│   ├── copilot-instructions.md
│   ├── workflows/
│   │   └── flutter_local_pipeline.yml
│   └── bots/
│       ├── architect.bot.md
│       ├── validator.bot.md
│       ├── security.bot.md
│       ├── monetization.bot.md
│       ├── optimization.bot.md
│       └── build.bot.md
│
├── lib/
│   ├── core/
│   │   ├── config/
│   │   ├── constants/
│   │   ├── errors/
│   │   ├── utils/
│   │   └── storage/
│   │       └── local_storage.dart
│   │
│   ├── data/
│   │   ├── datasources/local/
│   │   ├── models/
│   │   └── repositories/
│   │
│   ├── domain/
│   │   ├── entities/
│   │   ├── repositories/
│   │   └── usecases/
│   │
│   ├── presentation/
│   │   ├── pages/
│   │   │   └── home_page.dart
│   │   ├── widgets/
│   │   └── state/
│   │       └── app_state.dart
│   │
│   └── main.dart
│
├── pubspec.yaml
└── README.md
🔒 Esta estructura no se vuelve a tocar.
3. pubspec.yaml (Dependencias Premium Aprobadas)
Copiar código
Yaml
dependencies:
  flutter:
    sdk: flutter

  flutter_riverpod: ^2.5.0
  shared_preferences: ^2.2.2
  path_provider: ^2.1.2

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0
⛔ Nada más.
⛔ Ninguna dependencia dinámica.
4. Storage Local Idempotente (Base del Sistema)
lib/core/storage/local_storage.dart
Copiar código
Dart
import 'package:shared_preferences/shared_preferences.dart';

class LocalStorage {
  static const _premiumKey = 'is_premium';

  Future<bool> isPremium() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_premiumKey) ?? false;
  }

  Future<void> setPremium(bool value) async {
    final prefs = await SharedPreferences.getInstance();
    if ((prefs.getBool(_premiumKey) ?? false) != value) {
      await prefs.setBool(_premiumKey, value);
    }
  }
}
✔ Idempotente
✔ Local
✔ Seguro
✔ Play Store friendly
5. Estado Global (Riverpod, ÚNICO)
lib/presentation/state/app_state.dart
Copiar código
Dart
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/storage/local_storage.dart';

final localStorageProvider = Provider((_) => LocalStorage());

final premiumProvider = FutureProvider<bool>((ref) async {
  final storage = ref.read(localStorageProvider);
  return storage.isPremium();
});
6. main.dart (Inicialización Limpia)
Copiar código
Dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'presentation/pages/home_page.dart';

void main() {
  runApp(const ProviderScope(child: MyApp()));
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: HomePage(),
    );
  }
}
7. UI Base (Funcional, Sin Excesos)
lib/presentation/pages/home_page.dart
Copiar código
Dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../state/app_state.dart';

class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final premium = ref.watch(premiumProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Flutter Local App')),
      body: Center(
        child: premium.when(
          data: (isPremium) =>
              Text(isPremium ? 'Modo Premium' : 'Modo Gratuito'),
          loading: () => const CircularProgressIndicator(),
          error: (_, __) => const Text('Error'),
        ),
      ),
    );
  }
}
8. Automatización LOCAL (Idéntica al CI)
scripts/local_pipeline.sh
Copiar código
Bash
#!/bin/bash
set -e

flutter doctor -v
flutter pub get
flutter analyze
flutter test
flutter build appbundle --release
✔ Local
✔ Offline
✔ Idempotente
9. Pipeline CI (GitHub Actions)
(se integra tal cual el que ya te entregué, sin cambios)
10. Checklist Play Store FINAL (Automático + Humano)
✔ App funciona sin internet
✔ Sin permisos sensibles
✔ Política de privacidad simple
✔ Capturas reales
✔ Descripción clara
✔ AAB generado en release
11. Resultado Final
🎯 Salida única del sistema:
Copiar código

build/app/outputs/bundle/release/app-release.aab
✔ Publicable
✔ Vendible
✔ Aprobable
✔ Mantenible
12. Clausura Definitiva
Este es el flujo PREMIUM FINAL.
Desde la primera letra
hasta la app lista en Play Store.
No hay más capas.
No hay más sugerencias.
No hay mejoras pendientes.
Sistema cerrado.
Fin absoluto.
