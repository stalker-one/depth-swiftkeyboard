'use client'

import {
  Check,
  Clipboard,
  Download,
  Globe2,
  Languages,
  Palette,
  ShieldCheck,
  Sparkles,
  Sticker,
  SunMoon,
  WandSparkles,
  Zap,
} from 'lucide-react'

const apkDownloadUrl = 'https://github.com/stalker-one/depth-swiftkeyboard/releases/download/Depth-SwiftKeyboard/depth-keyboard.apk'

const features = [
  { icon: WandSparkles, title: 'Smart suggestions', text: 'Prediction-ready typing with autocorrect that helps without getting in your way.' },
  { icon: Sticker, title: 'Emoji, GIFs & stickers', text: 'Express yourself faster with a rich toolbar built for everyday conversations.' },
  { icon: Clipboard, title: 'Clipboard shortcuts', text: 'Keep useful phrases close and paste them with one tap when you need them.' },
  { icon: Languages, title: 'Multilingual typing', text: 'Switch between languages quickly while you write and stay in your flow.' },
  { icon: Palette, title: 'Personal themes', text: 'Choose Aurora, Midnight, or Sand and make the keyboard feel like yours.' },
  { icon: Zap, title: 'Quick controls', text: 'Keep language, symbols, emoji, and clipboard controls one tap away.' },
]

const steps = [
  ['Install', 'Download the APK and open it on your Android phone.'],
  ['Enable', 'Open the app, tap Enable Depth Keyboard, then allow it in Android Settings.'],
  ['Choose', 'Tap Choose keyboard and select Depth Keyboard as your active input method.'],
]

export default function DepthKeyboard() {
  return (
    <main className="depth-app feature-home">
      <header className="topbar">
        <div className="brand"><div className="brand-mark"><Zap size={17} /></div><div><p className="eyebrow">DEPTH LABS</p><h1>Depth Keyboard</h1></div></div>
        <div className="top-actions"><a className="header-download" href={apkDownloadUrl} download="depth-keyboard.apk"><Download size={15} /> Get the APK</a><div className="avatar" aria-label="Depth Keyboard">DK</div></div>
      </header>

      <section className="hero-section">
        <div className="hero-copy">
          <p className="eyebrow">A KEYBOARD MADE FOR YOUR FLOW</p>
          <h2>Type faster.<br /><em>Stay in the moment.</em></h2>
          <p className="hero-text">Depth Keyboard brings thoughtful predictions, expressive tools, and a calm, personal typing experience to Android.</p>
          <div className="hero-actions"><a className="download-button hero-button" href={apkDownloadUrl} download="depth-keyboard.apk"><Download size={17} /> Download for Android</a><a className="release-link" href="https://github.com/stalker-one/depth-swiftkeyboard/releases/tag/Depth-SwiftKeyboard">View release notes <span>↗</span></a></div>
          <div className="trust-row"><span><ShieldCheck size={15} /> On-device first</span><span><Check size={15} /> Free to try</span><span><Globe2 size={15} /> Android 8+</span></div>
        </div>
        <div className="hero-art" aria-label="Abstract Depth Keyboard feature illustration">
          <div className="float-chip chip-ai"><Sparkles size={14} /> Smart AI-ready typing</div><div className="float-chip chip-emoji">🔥 <span>Favorites learned</span></div>
          <div className="art-glow" /><div className="art-panel"><div className="art-top"><span className="art-dot" /><span className="art-dot" /><span className="art-dot" /><span>Depth Keyboard</span></div><div className="art-suggestion"><strong>Beautiful</strong><span>brilliant</span><span>better</span><Sparkles size={14} /></div><div className="art-keys">{['q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m'].map((key) => <span key={key}>{key}</span>)}</div><div className="art-bottom"><span>☺</span><span>🌐</span><b>space</b><span>⌫</span><i>↵</i></div></div>
        </div>
      </section>

      <div className="capability-rail" aria-label="Keyboard capabilities"><div className="capability-track">{['Tap to type', 'Smart suggestions', 'Emoji tools', 'Clipboard', 'Multilingual typing', 'Personal themes', 'Quick controls', 'Tap to type', 'Smart suggestions', 'Emoji tools', 'Clipboard', 'Multilingual typing'].map((item, index) => <span key={`${item}-${index}`}>{item}<b>•</b></span>)}</div></div>

      <section className="feature-section"><div className="section-heading feature-heading"><div><p className="eyebrow">EVERYTHING YOU NEED</p><h2>More than a keyboard.</h2></div><p>Built around the little things that make typing feel effortless.</p></div><div className="feature-grid">{features.map(({ icon: Icon, title, text }) => <article className="feature-card" key={title}><div className="feature-icon"><Icon size={18} /></div><h3>{title}</h3><p>{text}</p></article>)}</div></section>

      <section className="spotlight-section"><div className="spotlight-copy"><p className="eyebrow">COMPOSE WITH CONFIDENCE</p><h2>From rough thought to ready-to-send.</h2><p>Depth is designed for the whole conversation: draft, rewrite, react, and send without leaving the app you are using.</p><div className="spotlight-pills"><span><WandSparkles size={14} /> Rewrite tones</span><span><Sparkles size={14} /> Smart compose</span><span><Sticker size={14} /> Rich content</span></div></div><div className="compose-card"><div className="compose-label">COMPOSE ASSISTANT <Sparkles size={13} /></div><p className="compose-draft">Can you send me the details when you have a moment?</p><div className="compose-actions"><button>Friendly</button><button>Professional</button><button>Shorter</button></div><div className="compose-result">Absolutely — send the details whenever you’re ready.</div></div></section>

      <section className="personal-section"><div><p className="eyebrow">MAKE IT YOURS</p><h2>Your style. Your languages. Your privacy.</h2><p>Switch themes, choose your languages, and tune sound or haptic feedback. Your typing stays personal and your controls stay close.</p></div><div className="theme-stack"><div className="mini-theme aurora-mini"><SunMoon size={16} /><span>Aurora</span><small>Soft and focused</small></div><div className="mini-theme midnight-mini"><SunMoon size={16} /><span>Midnight</span><small>Calm after dark</small></div><div className="mini-theme sand-mini"><SunMoon size={16} /><span>Sand</span><small>Warm and easy</small></div></div></section>

      <section className="install-section"><div className="section-heading feature-heading"><div><p className="eyebrow">START TYPING</p><h2>Ready in three steps.</h2></div><a className="download-button" href={apkDownloadUrl} download="depth-keyboard.apk"><Download size={16} /> Download APK</a></div><div className="steps-grid">{steps.map(([title, text], index) => <div className="step" key={title}><span>{index + 1}</span><h3>{title}</h3><p>{text}</p></div>)}</div></section>

      <footer className="site-footer"><span><Zap size={14} /> Depth Keyboard</span><span>Your data stays on your device.</span><a href="https://github.com/stalker-one/depth-swiftkeyboard">Open source on GitHub ↗</a></footer>
    </main>
  )
}
