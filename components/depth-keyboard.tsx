'use client'

import { useMemo, useState } from 'react'
import {
  ArrowLeft,
  Check,
  ChevronDown,
  Clipboard,
  Command,
  Copy,
  Delete,
  Ellipsis,
  Globe2,
  Image as ImageIcon,
  Languages,
  Mic,
  Moon,
  Palette,
  Plus,
  RotateCcw,
  Search,
  Settings2,
  Smile,
  Sparkles,
  Sticker,
  Sun,
  Timer,
  Volume2,
  WandSparkles,
  Zap,
} from 'lucide-react'

const letters = [
  ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
  ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'],
  ['z', 'x', 'c', 'v', 'b', 'n', 'm'],
]
const suggestions = ['the', 'and', 'you']
const emojis = ['☺', '😂', '♥', '👍', '🔥', '✨', '🎉', '🤔', '😭', '😎', '🙏', '💡']

function Key({ children, onClick, wide = false, active = false, className = '' }: { children: React.ReactNode; onClick?: () => void; wide?: boolean; active?: boolean; className?: string }) {
  return <button type="button" onClick={onClick} className={`key ${wide ? 'key-wide' : ''} ${active ? 'key-active' : ''} ${className}`} aria-label={typeof children === 'string' ? children : undefined}>{children}</button>
}

export default function DepthKeyboard() {
  const [text, setText] = useState('Start typing with Depth Keyboard')
  const [caps, setCaps] = useState(false)
  const [numbers, setNumbers] = useState(false)
  const [panel, setPanel] = useState<'keyboard' | 'emoji' | 'gif' | 'clipboard' | 'translate'>('keyboard')
  const [theme, setTheme] = useState<'aurora' | 'midnight' | 'sand'>('aurora')
  const [language, setLanguage] = useState('EN')
  const [autocorrect, setAutocorrect] = useState(true)
  const [sound, setSound] = useState(true)

  const displayText = useMemo(() => text || 'Tap a key to begin', [text])
  const type = (value: string) => setText((current) => current === 'Tap a key to begin' ? value : current + value)
  const backspace = () => setText((current) => current.slice(0, -1))
  const chooseSuggestion = (word: string) => setText((current) => `${current.trimEnd()} ${word}`)
  const togglePanel = (next: typeof panel) => setPanel(panel === next ? 'keyboard' : next)

  return (
    <main className={`depth-app theme-${theme}`}>
      <header className="topbar">
        <div className="brand"><div className="brand-mark"><Zap size={17} /></div><div><p className="eyebrow">DEPTH LABS</p><h1>Depth Keyboard</h1></div></div>
        <div className="top-actions"><button className="icon-button" type="button" aria-label="Search"><Search size={17} /></button><button className="icon-button" type="button" aria-label="Settings"><Settings2 size={17} /></button><button className="avatar" type="button" aria-label="Profile">AK</button></div>
      </header>

      <div className="workspace">
        <section className="demo-column">
          <div className="section-heading"><div><p className="eyebrow">LIVE PREVIEW</p><h2>Your keyboard, your flow.</h2></div><span className="live-pill"><span /> Live</span></div>
          <div className="typing-card">
            <div className="editor-top"><span className="editor-dot" /><span className="editor-dot" /><span className="editor-dot" /><span className="editor-label">Message</span><Ellipsis size={17} /></div>
            <div className="editor-body"><p className="typing-copy">{displayText}<span className="caret" /></p><div className="editor-meta"><span>To: Alex</span><span>{text.length} characters</span></div></div>
            <div className="editor-tools"><button type="button" onClick={() => setText('')}><RotateCcw size={15} /> Clear</button><button type="button"><Copy size={15} /> Copy</button><span className="editor-send"><Check size={15} /> Ready to send</span></div>
          </div>
          <div className="keyboard-shell">
            <div className="toolbar"><button type="button" onClick={() => togglePanel('emoji')} className={panel === 'emoji' ? 'toolbar-active' : ''}><Smile size={19} /></button><button type="button" onClick={() => togglePanel('gif')} className={panel === 'gif' ? 'toolbar-active' : ''}><Sticker size={19} /></button><button type="button" onClick={() => togglePanel('clipboard')} className={panel === 'clipboard' ? 'toolbar-active' : ''}><Clipboard size={18} /></button><button type="button" onClick={() => togglePanel('translate')} className={panel === 'translate' ? 'toolbar-active' : ''}><Languages size={18} /></button><span className="toolbar-spacer" /><button type="button" onClick={() => setTheme(theme === 'midnight' ? 'aurora' : 'midnight')}><Moon size={17} /></button><button type="button"><Mic size={18} /></button></div>
            {panel !== 'keyboard' && <div className="utility-panel">{panel === 'emoji' && <>{emojis.map((emoji) => <button type="button" key={emoji} onClick={() => type(emoji)}>{emoji}</button>)}</>}{panel === 'gif' && <><div className="utility-title"><span>GIFs & Stickers</span><Search size={15} /></div><div className="gif-grid"><span>MOOD</span><span>LOL</span><span>VIBES</span><span>THANKS</span></div></>}{panel === 'clipboard' && <><div className="utility-title"><span>Clipboard</span><Plus size={15} /></div><button type="button" className="clip-item" onClick={() => type('I am running a little late — see you soon!')}>I am running a little late — see you soon!</button><button type="button" className="clip-item" onClick={() => type('Sounds good, let’s do it.')}>Sounds good, let&apos;s do it.</button></>}{panel === 'translate' && <><div className="translate-row"><span>English</span><ArrowLeft size={15} /><span>Spanish</span></div><button type="button" className="translate-result" onClick={() => setText('Hola, ¿cómo estás?')}>Hola, ¿cómo estás?</button></>}</div>}
            {panel === 'keyboard' && <>
              <div className="suggestion-row">{suggestions.map((suggestion, index) => <button type="button" key={suggestion} onClick={() => chooseSuggestion(suggestion)} className={index === 0 ? 'suggestion-main' : ''}>{suggestion}{index === 0 && <Sparkles size={12} />}</button>)}<ChevronDown size={16} /></div>
              <div className="keys-area">{numbers ? <><div className="key-row">{['1','2','3','4','5','6','7','8','9','0'].map((key) => <Key key={key} onClick={() => type(key)}>{key}</Key>)}</div><div className="key-row">{['@','#','$','%','&','*','(',')','-','+'].map((key) => <Key key={key} onClick={() => type(key)}>{key}</Key>)}</div></> : letters.map((row) => <div className="key-row" key={row.join('')}>{row.map((key) => <Key key={key} onClick={() => type(caps ? key.toUpperCase() : key)}>{caps ? key.toUpperCase() : key}</Key>)}</div>)}
                <div className="key-row bottom-row"><Key onClick={() => setNumbers(!numbers)} className="secondary-key">{numbers ? 'ABC' : '123'}</Key><Key onClick={() => setCaps(!caps)} active={caps} className="secondary-key">⇧</Key><Key onClick={() => type(' ')} wide className="space-key">space</Key><Key onClick={backspace} className="secondary-key"><Delete size={18} /></Key><Key onClick={() => type('\n')} className="enter-key"><span>↵</span></Key></div>
              </div>
            </>}
            <div className="keyboard-footer"><span><Globe2 size={14} /> {language}</span><span>Depth Keyboard <span className="footer-dot">•</span> {sound ? 'Sound on' : 'Silent'}</span></div>
          </div>
        </section>

        <aside className="settings-column">
          <div className="settings-heading"><div><p className="eyebrow">PERSONALIZE</p><h2>Make it yours.</h2></div><Palette size={19} /></div>
          <div className="settings-card"><div className="card-label"><Palette size={16} /> Themes</div><div className="theme-grid">{(['aurora', 'midnight', 'sand'] as const).map((item) => <button type="button" key={item} onClick={() => setTheme(item)} className={`theme-choice theme-choice-${item} ${theme === item ? 'selected' : ''}`}><span className="theme-preview"><i /><i /><i /></span><span>{item[0].toUpperCase() + item.slice(1)}</span>{theme === item && <Check size={14} />}</button>)}</div></div>
          <div className="settings-card"><div className="card-label"><WandSparkles size={16} /> Smart typing</div>{[['Autocorrect', 'Fix typos as you type', autocorrect, setAutocorrect], ['Predictions', 'Next-word suggestions', true, () => {}]].map(([name, desc, enabled, setEnabled]) => <div className="setting-row" key={name as string}><div><strong>{name as string}</strong><small>{desc as string}</small></div><button type="button" className={`switch ${enabled ? 'on' : ''}`} onClick={() => (setEnabled as (value: boolean) => void)(!enabled)} aria-label={`Toggle ${name}`}><span /></button></div>)}</div>
          <div className="settings-card"><div className="card-label"><Globe2 size={16} /> Languages</div><div className="language-row"><button type="button" className={language === 'EN' ? 'language-active' : ''} onClick={() => setLanguage('EN')}>English <small>EN</small></button><button type="button" className={language === 'ES' ? 'language-active' : ''} onClick={() => setLanguage('ES')}>Español <small>ES</small></button><button type="button" className="add-language" aria-label="Add language"><Plus size={17} /></button></div><button type="button" className="manage-link"><Languages size={14} /> Manage languages</button></div>
          <div className="settings-card compact-card"><div className="card-label"><Volume2 size={16} /> Key feedback</div><div className="feedback-options"><button type="button" className={sound ? 'feedback-active' : ''} onClick={() => setSound(true)}><Volume2 size={15} /> Sound</button><button type="button" className={!sound ? 'feedback-active' : ''} onClick={() => setSound(false)}><Timer size={15} /> Haptic</button></div></div>
          <p className="privacy-note"><Command size={14} /> Your data stays on your device.</p>
        </aside>
      </div>
    </main>
  )
}
