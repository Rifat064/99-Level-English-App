import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Settings',
      theme: ThemeData(
        // Theme of cream + yellow/orange, dark navy blue structure
        scaffoldBackgroundColor: const Color(0xFFFFFDD0), // Cream
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFFFFA500), // Orange/Yellow seed
          primary: const Color(0xFF000080), // Navy Blue
          secondary: const Color(0xFFFFD700), // Yellow
          surface: Colors.white,
        ),
        useMaterial3: true,
      ),
      home: const SettingsDashboard(),
    );
  }
}

class SettingsDashboard extends StatefulWidget {
  const SettingsDashboard({super.key});

  @override
  State<SettingsDashboard> createState() => _SettingsDashboardState();
}

class _SettingsDashboardState extends State<SettingsDashboard> {
  bool notificationsEnabled = true;
  bool darkThemeEnabled = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Settings',
          style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
        ),
        backgroundColor: const Color(0xFF000080), // Navy blue
        elevation: 0,
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            // Send message to native Android to close the Flutter activity
            import('package:flutter/services.dart').then((services) {
               services.SystemNavigator.pop();
            });
          },
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildSectionHeader('Preferences'),
            _buildCard(
              child: Column(
                children: [
                  SwitchListTile(
                    title: const Text('Push Notifications', style: TextStyle(fontWeight: FontWeight.w600)),
                    subtitle: const Text('Receive daily word reminders', style: TextStyle(color: Colors.black54)),
                    activeColor: const Color(0xFFFFA500),
                    value: notificationsEnabled,
                    onChanged: (bool value) {
                      setState(() {
                        notificationsEnabled = value;
                      });
                    },
                    secondary: const Icon(Icons.notifications_active, color: Color(0xFF000080)),
                  ),
                  const Divider(height: 1),
                  SwitchListTile(
                    title: const Text('Dark Mode', style: TextStyle(fontWeight: FontWeight.w600)),
                    subtitle: const Text('Toggle app appearance', style: TextStyle(color: Colors.black54)),
                    activeColor: const Color(0xFFFFA500),
                    value: darkThemeEnabled,
                    onChanged: (bool value) {
                      setState(() {
                        darkThemeEnabled = value;
                      });
                    },
                    secondary: const Icon(Icons.dark_mode, color: Color(0xFF000080)),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            _buildSectionHeader('Account'),
            _buildCard(
              child: Column(
                children: [
                  ListTile(
                    leading: const CircleAvatar(
                      backgroundColor: Color(0xFFFFD700), // Yellow
                      child: Icon(Icons.person, color: Color(0xFF000080)),
                    ),
                    title: const Text('Guest Profile', style: TextStyle(fontWeight: FontWeight.w600)),
                    subtitle: const Text('Tap to sign in and save progress', style: TextStyle(color: Colors.black54)),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () {},
                  ),
                  const Divider(height: 1),
                  ListTile(
                    leading: const Icon(Icons.star, color: Color(0xFFFFA500)),
                    title: const Text('Upgrade to Premium', style: TextStyle(fontWeight: FontWeight.w600)),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () {},
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            _buildSectionHeader('Support'),
            _buildCard(
              child: Column(
                children: [
                  ListTile(
                    leading: const Icon(Icons.help_outline, color: Color(0xFF000080)),
                    title: const Text('Help Center', style: TextStyle(fontWeight: FontWeight.w600)),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () {},
                  ),
                  const Divider(height: 1),
                  ListTile(
                    leading: const Icon(Icons.privacy_tip_outlined, color: Color(0xFF000080)),
                    title: const Text('Privacy Policy', style: TextStyle(fontWeight: FontWeight.w600)),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () {},
                  ),
                ],
              ),
            ),
            const SizedBox(height: 48),
            Center(
              child: TextButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.logout, color: Colors.redAccent),
                label: const Text(
                  'Sign Out',
                  style: TextStyle(color: Colors.redAccent, fontSize: 16, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.only(left: 8.0, bottom: 8.0),
      child: Text(
        title.toUpperCase(),
        style: const TextStyle(
          color: Color(0xFF000080), // Navy Blue
          fontWeight: FontWeight.bold,
          letterSpacing: 1.2,
          fontSize: 12,
        ),
      ),
    );
  }

  Widget _buildCard({required Widget child}) {
    return Card(
      elevation: 4,
      shadowColor: const Color(0xFFFFA500).withOpacity(0.3),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: child,
    );
  }
}
