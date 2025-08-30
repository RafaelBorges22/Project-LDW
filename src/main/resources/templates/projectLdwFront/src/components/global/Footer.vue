<script setup lang="ts">
import { ref } from 'vue'

const navLinks = [
  { name: 'Home', href: '#' },
  { name: 'Orçamento', href: '#' },
  { name: 'Pós-Tattoo', href: '#' },
  { name: 'Meu Trabalho', href: '#' }
]

const socialLinks = [
  { name: 'Whatsapp', href: '#', icon: 'whatsapp' },
  { name: 'Instagram', href: '#', icon: 'instagram' }
]
</script>

<template>
  <footer class="bg-kazu-black relative">
    <div class="bg-kazu-brown h-8 md:h-9"></div>
    
    <div class="px-4 py-12 md:py-16 lg:py-20">
      <div class="max-w-7xl mx-auto">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 lg:gap-16">
          <div class="flex justify-center lg:justify-start">
            <img 
              src="https://api.builder.io/api/v1/image/assets/TEMP/06439fe35e7f8931ec5b1991622d1f0eabd20d08?width=730" 
              alt="Kazu Tattoo Logo" 
              class="w-48 h-48 md:w-64 md:h-64 lg:w-80 lg:h-80"
            />
          </div>
          
          <div class="text-center lg:text-left">
            <h4 class="text-white text-2xl md:text-3xl lg:text-5xl xl:text-6xl font-normal mb-6 md:mb-8">
              Links
            </h4>
            <nav class="space-y-4 md:space-y-6">
              <a 
                v-for="link in navLinks" 
                :key="link.name"
                :href="link.href" 
                class="block text-kazu-gray text-lg md:text-xl lg:text-2xl xl:text-4xl font-light underline hover:text-white transition-colors tracking-wider"
              >
                {{ link.name }}
              </a>
            </nav>
          </div>
          
          <div class="text-center lg:text-left">
            <h4 class="text-white text-2xl md:text-3xl lg:text-5xl xl:text-6xl font-normal mb-6 md:mb-8">
              Contatos
            </h4>
            <div class="space-y-4 md:space-y-6">
              <a 
                href="#" 
                class="flex items-center justify-center lg:justify-start space-x-4 text-kazu-gray text-lg md:text-xl lg:text-2xl xl:text-4xl font-light underline hover:text-white transition-colors tracking-wider"
              >
                <span>Whatsapp</span>
              </a>
              <a 
                href="#" 
                class="flex items-center justify-center lg:justify-start space-x-4 text-kazu-gray text-lg md:text-xl lg:text-2xl xl:text-4xl font-light underline hover:text-white transition-colors tracking-wider"
              >
                <span>Instagram</span>
              </a>
            </div>
          </div>
        </div>
        
        <div class="mt-12 md:mt-16 lg:mt-20 pt-8 border-t border-white/20">
          <p class="text-kazu-gray text-center text-base md:text-lg lg:text-2xl xl:text-4xl font-light tracking-wider">
            © 2025 Kazu Tattoo. Todos os direitos reservados
          </p>
        </div>
      </div>
    </div>
  </footer>
</template>
